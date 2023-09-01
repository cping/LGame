package loon.an;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;
import loon.LSystem;
import loon.SoundImpl;
import loon.events.Updateable;
import loon.utils.StringUtils;

public class JavaANAudio {

    protected static <I> void dispatchLoaded(final SoundImpl<I> sound, final I impl) {
        Updateable update = new Updateable() {
            @Override
            public void action(Object a) {
                sound.onLoaded(impl);
            }
        };
        LSystem.unload(update);
    }

    protected static <I> void dispatchLoadError(final SoundImpl<I> sound, final Throwable error) {
        Updateable update = new Updateable() {
            @Override
            public void action(Object a) {
                sound.onLoadError(error);
            }
        };
        LSystem.unload(update);
    }

    interface Resolver<I> {
        void resolve(JavaANSound<I> sound);
    }

    private final HashSet<JavaANSound<?>> playing = new HashSet<JavaANSound<?>>();
    private final HashMap<Integer, PooledSound> loadingSounds = new HashMap<Integer, PooledSound>();
    private final android.media.SoundPool pool;

    private class PooledSound extends SoundImpl<Integer> {
        public final int soundId;
        private int streamId;

        public PooledSound(int soundId) {
            this.soundId = soundId;
        }

        @Override
        public String toString() {
            return "pooled:" + soundId;
        }

        @Override
        protected boolean playingImpl() {
            return false;
        }

        @Override
        protected boolean playImpl() {
            streamId = pool.play(soundId, volume, volume, 1, looping ? -1 : 0, 1);
            return (streamId != 0);
        }

        protected boolean prepareImpl() {
            pool.play(soundId, 0, 0, 0, 0, 1);
            return true;
        }

        @Override
        protected void stopImpl() {
            if (streamId != 0) {
                pool.stop(streamId);
                streamId = 0;
            }
        }

        @Override
        protected void setLoopingImpl(boolean looping) {
            if (streamId != 0) {
                pool.setLoop(streamId, looping ? -1 : 0);
            }
        }

        @Override
        protected void setVolumeImpl(float volume) {
            if (streamId != 0) {
                pool.setVolume(streamId, volume, volume);
            }
        }

        @Override
        protected void releaseImpl() {
            pool.unload(soundId);
        }

        @Override
        public boolean pause() {
            prepareImpl();
            return true;
        }
    };

    private final JavaANGame game;

    @SuppressWarnings("deprecation")
    public JavaANAudio(JavaANGame game) {
        this.game = game;
        if (JavaANGame.isAndroidVersionHigher(21)) {
            android.media.AudioAttributes audioAttributes = null;
            audioAttributes = new android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC).build();
            this.pool = new android.media.SoundPool.Builder().setMaxStreams(16).setAudioAttributes(audioAttributes)
                    .build();
        } else {
            this.pool = new android.media.SoundPool(16, AudioManager.STREAM_MUSIC, 0);
        }
        // 以标准pool监听器监听数据
        this.pool.setOnLoadCompleteListener(new android.media.SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(android.media.SoundPool soundPool, int soundId, int status) {
                PooledSound sound = loadingSounds.remove(soundId);
                if (sound == null) {
                    Log.e("AndroidAudio", "load _complete for unknown sound [id=" + soundId + "]");
                } else if (status == 0) {
                    dispatchLoaded(sound, soundId);
                } else {
                    dispatchLoadError(sound, new Exception("Sound load failed [errcode=" + status + "]"));
                }
            }
        });
    }

    public SoundImpl<?> createSound(AssetFileDescriptor fd) {
        PooledSound sound = new PooledSound(pool.load(fd, 1));
        loadingSounds.put(sound.soundId, sound);
        return sound;
    }

    public SoundImpl<?> createSound(FileDescriptor fd, long offset, long length) {
        PooledSound sound = new PooledSound(pool.load(fd, offset, length, 1));
        loadingSounds.put(sound.soundId, sound);
        return sound;
    }

    private AssetFileDescriptor openFd(String fileName) throws IOException {
        if (game.isMobile()) {
            if (fileName.toLowerCase().startsWith("assets/")) {
                fileName = StringUtils.replaceIgnoreCase(fileName, "assets/", "");
            }
            if (fileName.startsWith("/") || fileName.startsWith("\\")) {
                fileName = fileName.substring(1, fileName.length());
            }
        }
        return ((JavaANAssets)((JavaANGame)game).assets()).openAssetFd(fileName);
    }

    public SoundImpl<?> createSound(final String path) {
        try {
            return createSound(openFd(path));
        } catch (IOException ioe) {
            PooledSound sound = new PooledSound(0);
            sound.onLoadError(ioe);
            return sound;
        }
    }

    public SoundImpl<?> createMusic(final String path) {
        return new JavaANBigClip(this, new Resolver<MediaPlayer>() {
            @Override
            public void resolve(final JavaANSound<MediaPlayer> sound) {
                final MediaPlayer mp = new MediaPlayer();
                LSystem.load(new Updateable() {
                    @Override
                    public void action(Object o) {
                        try {
                            AssetFileDescriptor fd = openFd(path);
                            mp.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                            fd.close();
                            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(final MediaPlayer mp) {
                                    dispatchLoaded(sound, mp);
                                }
                            });
                            mp.setOnErrorListener(new OnErrorListener() {

                                @Override
                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                    String errmsg = "MediaPlayer prepare failure [what=" + what + ", x=" + extra + "]";
                                    dispatchLoadError(sound, new Exception(errmsg));
                                    return false;
                                }
                            });
                            mp.prepareAsync();
                        } catch (Exception e) {
                            dispatchLoadError(sound, e);
                        }
                    }
                });
            }
        });
    }

    public void onPause() {
        for (PooledSound p : loadingSounds.values()) {
            pool.pause(p.soundId);
        }
        for (JavaANSound<?> sound : playing) {
            sound.onPause();
        }
    }

    public void onResume() {
        pool.autoResume();
        HashSet<JavaANSound<?>> wasPlaying = new HashSet<JavaANSound<?>>(playing);
        playing.clear();
        if (!wasPlaying.isEmpty()) {
            Log.e("AndroidAudio", "Resuming " + wasPlaying.size() + " playing sounds.");
        }
        for (JavaANSound<?> sound : wasPlaying) {
            sound.onResume();
        }
    }

    public void onDestroy() {
        for (JavaANSound<?> sound : playing) {
            sound.release();
        }
        playing.clear();
        pool.release();
    }

    void onPlaying(JavaANSound<?> sound) {
        playing.add(sound);
    }

    void onStopped(JavaANSound<?> sound) {
        playing.remove(sound);
    }
}

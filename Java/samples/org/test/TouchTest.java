package org.test;

import loon.LTexture;
import loon.Stage;
import loon.geom.RectBox;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.TimeUtils;

public class TouchTest extends Stage{
	private LTexture dropImage;
	private LTexture bucketImage;

	private RectBox bucket;
	private TArray<RectBox> raindrops;
	private long lastDropTime;

	public static int SCREEN_WIDTH = 544;
	public static int SCREEN_HEIGHT = 816;

	private static int BUCKET_WIDTH_HEIGHT = 64;
	private static int DROP_WIDTH_HEIGHT = 64;

	private static int BUCKET_Y_POSITION = 20;

	private static long DROP_RESPAWN_TIME_IN_MILLIS = 1000000000/2;

	private static int GAME_VELOCITY = 200;
	@Override
	public void create() {
	
				dropImage = loadTexture("droplet.png");
				bucketImage = loadTexture("bucket.png");
				putRelease(dropImage);
				putRelease(bucketImage);
				
				bucket = new RectBox();
				bucket.x = SCREEN_WIDTH / 2 - BUCKET_WIDTH_HEIGHT / 2; 
				bucket.y = BUCKET_Y_POSITION; 
				bucket.width = BUCKET_WIDTH_HEIGHT;
				bucket.height = BUCKET_WIDTH_HEIGHT;

				raindrops = new TArray<RectBox>();
		
	}


	private void spawnRaindrop() {
		RectBox raindrop = new RectBox();
		raindrop.x = MathUtils.random(0, SCREEN_WIDTH - DROP_WIDTH_HEIGHT);
		raindrop.y = SCREEN_HEIGHT;
		raindrop.width = DROP_WIDTH_HEIGHT;
		raindrop.height = DROP_WIDTH_HEIGHT;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
	
}

namespace Loon
{
    public interface XNABind
    {
        /// <summary>
        /// LGame初始化构建函数
        /// </summary>
        void OnMain();

        void OnGameResumed();

        void OnGamePaused();

        void OnGameExit();

        void OnCreate(bool m_landscape, bool m_fullscreen);

        void OnStateLog(Loon.Utils.Debug.Log log);

        GameType GetGameType();

    }
}

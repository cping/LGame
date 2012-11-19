namespace Loon.Core.Input
{
    /// <summary>
    /// C#(XNA)版特有接口，用以监听XBOX游戏机手柄输入
    /// </summary>
    public interface GamePadListener
    {

        void DPadDown_Pressed(int playIndex);

        void DPadLeft_Pressed(int playIndex);

        void DPadUp_Pressed(int playIndex);

        void DPadRight_Pressed(int playIndex);

        void LeftThumbstickDown_Pressed(int playIndex);

        void LeftThumbstickLeft_Pressed(int playIndex);

        void LeftThumbstickUp_Pressed(int playIndex);

        void LeftThumbstickRight_Pressed(int playIndex);

        void RightThumbstickDown_Pressed(int playIndex);

        void RightThumbstickLeft_Pressed(int playIndex);

        void RightThumbstickUp_Pressed(int playIndex);

        void RightThumbstickRight_Pressed(int playIndex);

        void LeftTrigger_Pressed(int playIndex);

        void RightTrigger_Pressed(int playIndex);

        void A_Pressed(int playIndex);

        void B_Pressed(int playIndex);

        void Back_Pressed(int playIndex);

        void BigButton_Pressed(int playIndex);

        void LeftShoulder_Pressed(int playIndex);

        void LeftStick_Pressed(int playIndex);

        void RightShoulder_Pressed(int playIndex);

        void RightStick_Pressed(int playIndex);

        void Start_Pressed(int playIndex);

        void X_Pressed(int playIndex);

        void Y_Pressed(int playIndex);


        void DPadDown_Released(int playIndex);

        void DPadLeft_Released(int playIndex);

        void DPadUp_Released(int playIndex);

        void DPadRight_Released(int playIndex);

        void LeftThumbstickDown_Released(int playIndex);

        void LeftThumbstickLeft_Released(int playIndex);

        void LeftThumbstickUp_Released(int playIndex);

        void LeftThumbstickRight_Released(int playIndex);

        void RightThumbstickDown_Released(int playIndex);

        void RightThumbstickLeft_Released(int playIndex);

        void RightThumbstickUp_Released(int playIndex);

        void RightThumbstickRight_Released(int playIndex);

        void LeftTrigger_Released(int playIndex);

        void RightTrigger_Released(int playIndex);

        void A_Released(int playIndex);

        void B_Released(int playIndex);

        void Back_Released(int playIndex);

        void BigButton_Released(int playIndex);

        void LeftShoulder_Released(int playIndex);

        void LeftStick_Released(int playIndex);

        void RightShoulder_Released(int playIndex);

        void RightStick_Released(int playIndex);

        void Start_Released(int playIndex);

        void X_Released(int playIndex);

        void Y_Released(int playIndex);
    }
}

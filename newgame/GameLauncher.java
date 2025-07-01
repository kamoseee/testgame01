package newgame; // パッケージを統一

import javax.swing.*;

public class GameLauncher {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Bykin Scroll Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときの動作
        frame.setSize(1280, 720); //ウィンドウのサイズ
        frame.setResizable(false);//サイズ変更不可


        frame.add(new BykinGame()); // BykinGameクラスを追加
        frame.pack(); // パネルのサイズに合わせてウィンドウを調整
        frame.setLocationRelativeTo(null);//ウィンドウを画面の中央に配置
        frame.setVisible(true); //ウィンドウを表示
    }
}

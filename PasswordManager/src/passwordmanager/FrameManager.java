/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import ManagerBean.UserAccount;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Stack;
import javax.swing.JFrame;

/**
 *
 * @author Jonathan
 */
public class FrameManager {

    public UserAccount userAccount;
    protected Stack<JFrame> pageStack;

    public FrameManager(JFrame frame) {
        this.pageStack = new Stack();
        this.pageStack.push(frame);
        this.centreWindow(frame);
        this.userAccount = null;
    }

    public void NextPage(JFrame frame) {
        this.pageStack.peek().setVisible(false);
        this.pageStack.push(frame);
        this.centreWindow(frame);
        frame.setVisible(true);
    }

    public void PreviousPage() {
        this.pageStack.pop().dispose();
        this.pageStack.peek().setVisible(true);
        this.centreWindow(this.pageStack.peek());
    }

    public void centreWindow(JFrame frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
}

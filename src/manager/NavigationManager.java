package manager;

import ui.MainFrame;

public class NavigationManager {

    private static NavigationManager instance = new NavigationManager();
    private MainFrame mainFrame;

    private NavigationManager() {}

    public static NavigationManager getInstance() { return instance; }

    public void setMainFrame(MainFrame frame) { this.mainFrame = frame; }

    public void showPanel(String panelName) {
        if (mainFrame == null) {
            System.out.println("[NavigationManager] MainFrame이 등록되지 않았습니다.");
            return;
        }
        mainFrame.showCard(panelName);
    }
}

package iqq.app.ui.frame;

import iqq.api.bean.IMBuddy;
import iqq.api.bean.IMRoom;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMFrame;
import iqq.app.ui.frame.panel.chat.BasicPanel;
import iqq.app.ui.frame.panel.chat.ChatPane;
import iqq.app.ui.frame.panel.chat.RoomPanel;
import iqq.app.ui.frame.panel.chat.UserPanel;
import iqq.app.ui.manager.ChatManager;
import org.sexydock.tabs.ITabCloseButtonListener;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-10
 * License  : Apache License 2.0
 */
public class ChatFrame extends IMFrame {
    private static final Logger LOG = LoggerFactory.getLogger(ChatFrame.class);

    private ChatPane contentPane;
    private JTabbedPane tabbedPane;

    public ChatFrame() {
        initUI();
        initTabListener();
    }

    private void initUI() {
        contentPane = new ChatPane(this);
        tabbedPane = contentPane.getTabbedPane();
        setIMContentPane(contentPane);
        setTitle("与 承∮诺 的对话");
        setPreferredSize(new Dimension(660, 580));        // 首选大小
        pack();
    }

    /**
     * 安装皮肤
     *
     * @param skinService
     */
    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);
        this.contentPane.installSkin(skinService);
        setIconImage(skinService.getIconByKey("window/titleWIcon").getImage());

        // 更新每个tab中panel的皮肤
        for(int i=0; i<tabbedPane.getTabCount(); i++) {
            BasicPanel entityPanel = (BasicPanel) tabbedPane.getComponentAt(i);
            entityPanel.installSkin(skinService);
        }
    }


    private void initTabListener() {
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(isVisible()) {
                    if( tabbedPane.getTabCount( ) == 0)
                    {
                        // 如是没有了，直接关闭窗口
                        dispose();
                    } else {
                        BasicPanel entityPanel = (BasicPanel) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
                        entityPanel.installSkin(getSkinService());
                        String title = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
                        setTitle(getI18nService().getMessage("conversationTitle", title));
                    }
                }
            }
        });
        tabbedPane.putClientProperty(JhromeTabbedPaneUI.TAB_CLOSE_BUTTON_LISTENER
                , new ITabCloseButtonListener() {

            @Override
            public void tabCloseButtonPressed(JTabbedPane tabbedPane, int tabIndex) {
                // 关闭了一个tab，相当于关闭了一个对话
                BasicPanel entityPanel = (BasicPanel) tabbedPane.getComponentAt(tabIndex);

                tabbedPane.removeTabAt(tabIndex);
                ChatManager.removeChat(entityPanel.getEntity());
            }
        });
    }

    public void addBuddyPane(IMBuddy buddy, UserPanel entityPanel) {
        ImageIcon avatar = new ImageIcon(buddy.getAvatar());
        tabbedPane.addTab(buddy.getNick(), avatar, entityPanel);
        tabbedPane.setSelectedComponent(entityPanel);
        setTitle(getI18nService().getMessage("conversationTitle", buddy.getNick()));
    }

    public void addRoomPane(IMRoom room, RoomPanel entityPanel) {
        ImageIcon avatar =new ImageIcon(room.getAvatar());
        tabbedPane.addTab(room.getNick(), avatar, entityPanel);
        tabbedPane.setSelectedComponent(entityPanel);
        setTitle(getI18nService().getMessage("conversationTitle", room.getNick()));
    }

    public void setSelectedChat(BasicPanel entityPanel) {
        tabbedPane.setSelectedComponent(entityPanel);
    }

    @Override
    public void hide() {
        // 隐藏时清除当然所有对话
        clearChats();
        super.hide();
    }

    private void clearChats() {
        // 在管理器中清除当然所有对话
        ChatManager.clearChats();

        int count = tabbedPane.getTabCount();
        for (int i = 0; i < count; i++) {
            tabbedPane.removeTabAt(0);
        }
    }
}

/*
 * JBookView.java
 */
package com.wms.jbook;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The application's main frame.
 */
class Chapter extends JPanel {

    private String chapterName;
    private String chapterContents;
    private EpubContentPane contentPane;

    public Chapter(Navigator navigator, String name) {
        this.chapterName = name;
        contentPane = new EpubContentPane(navigator);
    }

    /**
     * @return the chapterName
     */
    public String getChapterName() {
        return chapterName;
    }

    /**
     * @return the chapterContents
     */
    public String getChapterContents() {
        return chapterContents;
    }

    /**
     * @param chapterContents the chapterContents to set
     */
    public void setChapterContents(String chapterContents) {
        this.chapterContents = chapterContents;
    }
}

public class JBookView extends FrameView {

    public enum JBookType {

        Unknown,
        Novel,
        Novella,
        ShortStory;
    }
    private JBookType myJBookType = JBookType.Unknown;
    private JBookLibrarian localLibrarian = null;
    private static Logger log = LoggerFactory.getLogger(JBookView.class);
    private String bookPath = "c:\\Books\\";
    private File previousDir = null;
    // private ArrayList<jChapter> chapters = new ArrayList<>();
    // private DefaultListModel<String> chapterListModel = new DefaultListModel<>();
    // private JBookType myJBookType = JBookType.Unknown;
    private EpubContentPane contentPane;
    int coverW = 500;
    int coverH = 700;
    Dimension coverDim = new Dimension(coverW, coverH);
    int frameW = coverH;
    int frameH = coverW + 100;
    Dimension frameDim = new Dimension(frameW, frameH);

    private void openJBook(String jBookPath) {
        File tFile = new File(jBookPath);
        Path tPath = tFile.toPath();
        localLibrarian = new JBookLibrarian(tPath);
        JBookKey testKey = this.localLibrarian.addBook(jBookPath, this.coverDim.width, this.coverDim.height);
        JBook testJBook = this.localLibrarian.getBook(testKey);

        this.showJBook(testJBook);

    }

    private void openJBook(File file) {
        localLibrarian = new JBookLibrarian(file.toPath());

        JBookKey testKey = this.localLibrarian.addBook(file, this.coverPanel.getWidth(), this.coverPanel.getHeight());
        JBook testJBook = this.localLibrarian.getBook(testKey);

        this.showJBook(testJBook);
    }

//    private void clearJBookContents() {
//        if (chapters.size() > 0) {
//            chapters = new ArrayList<>();
//        }
////        if( jBookContents.getTabCount() > 0 )
////        {
////            jBookContents.removeAll();
////        }
//        int numJBookTabs = jBookContents.getTabCount();
////        // chapters.remove(0);
////        // chapters.remove(1);
////        // chapters.remove(2);
//        for (int i = 2; i < numJBookTabs; i++) {
//            jBookContents.remove(i);
////            chapters.remove(i);
//        }
//
//    }
    private void showJBook(JBook jBookToOpen) {
        Icon coverIcon = null;
        JLabel cover = new JLabel();
        int frameW = this.coverPanel.getWidth();
        int frameH = this.coverPanel.getHeight();
        Navigator nav = jBookToOpen.getNavigator();
        // Clear the current, if any, JBook contents

        chapters = jBookToOpen.getChapters();
        /*
        if (chapters.size() > 0) {
        chapters = new ArrayList<>();
        }
         * 
         */
//        if( jBookContents.getTabCount() > 0 )
//        {
//            jBookContents.removeAll();
//        }
        int numJBookTabs = jBookContents.getTabCount();
//        // chapters.remove(0);
//        // chapters.remove(1);
//        // chapters.remove(2);
        for (int i = 2; i < numJBookTabs; i++) {
            jBookContents.remove(i);
//            chapters.remove(i);
        }
        if (jBookToOpen != null && jBookToOpen.getJBookKey().hasTitle()) {
            // System.out.println("Found Test Book, title is:  " + jBookToOpen.getTitle());
            // this.getFrame().setTitle(jBookToOpen.getTitle());
            setFrameTitle(jBookToOpen);
            Resource imageResource = jBookToOpen.getCover();
            InputStream imageIS;
            try {
                if (imageResource != null) {
                    imageIS = imageResource.getInputStream();
//                coverIcon = createImageIcon(imageIS, JBookPath, frameW, frameH);
                    Image jBookImage = jBookToOpen.getCoverImage();
                    coverIcon = new ImageIcon(jBookImage);
                }

            } catch (IOException ex) {
                // ex.printStackTrace();
            }

            if (coverIcon != null) {
                coverPanel.removeAll();
                cover.setIcon(coverIcon);
                cover.setBackground(Color.BLACK);
                this.coverPanel.add(cover);
            }
        }
        setInfo(jBookToOpen);
        // clearJBookContents();

//            createTOC(jBookToOpen);
        if (jBookToOpen == null) {
            return;
        }
        // this.tocPanelList.removeAll();
        this.chapterListModel.removeAllElements();
        TableOfContents toc = jBookToOpen.getTOC();
        List<TOCReference> tocr = toc.getTocReferences();
        StringBuilder tocs = new StringBuilder();
        // String tocAsString = jBookToOpen.getBook().getContents().toString();

        Iterator<TOCReference> it = tocr.iterator();
        while (it.hasNext()) {
            if (myJBookType.equals(JBookType.Unknown)) {
                myJBookType = JBookType.ShortStory;
            }
            TOCReference tocRefLine = it.next();
            String tocLine = tocRefLine.getTitle();
            if (tocLine.toLowerCase().contains("chapter")) {
                this.myJBookType = JBookType.Novel;
            }

//                jChapter newChapter = new jChapter(nav, tocLine);
//                this.chapters.add(newChapter);
//                int contIndex = chapters.size();
//                System.out.println("Chapter Index is: " + contIndex);
//                String chapCont = jBookToOpen.getChapter(contIndex);
//                newChapter.setChapterContents(chapCont);


//            System.out.println("TOC Line: " + tocLine );
            if (tocLine != null) {
                //  JLabel tocJLine = new JLabel(tocLine);
                this.chapterListModel.addElement(tocLine);
                // this.tocPanelList.add(tocJLine);
                //JLabel tocLineLabel = new JLabel(tocLine.toString());
                //this.tocTextArea.add( tocLineLabel);
                // this.tocTextArea.append(tocLine);
            }

        }
        // clearJBookContents();
        // this.buildJBookContents();
        Iterator<jChapter> ite = chapters.iterator();
        while (ite.hasNext()) {
            jChapter chap = ite.next();
            String tabName = chap.getChapterName();
            jBookContents.add(tabName, chap);
        }
        this.jBookContents.setSelectedIndex(0);
        this.setStatus("JBook Loaded !");

    }

//    private void buildJBookContents() {
//        Iterator<jChapter> it = chapters.iterator();
//        while (it.hasNext()) {
//            jChapter chap = it.next();
//            String tabName = chap.getChapterName();
//            jBookContents.add(tabName, chap);
//        }
//    }
    private void setFrameTitle(JBook jBookToSetTitleOf) {
        if (jBookToSetTitleOf != null) {
            System.out.println("Found Book, title is:  " + jBookToSetTitleOf.getTitle());
            this.getFrame().setTitle(jBookToSetTitleOf.getTitle());
        } else {
            System.out.println("Book not found  " + jBookToSetTitleOf);
            this.getFrame().setTitle("Book not found");
        }
    }

//    private void createTOC(JBook jBookToOpen) throws IOException {
//        if (jBookToOpen == null) {
//            return;
//        }
//        // this.tocPanelList.removeAll();
//        this.chapterListModel.removeAllElements();
//        TableOfContents toc = jBookToOpen.getTOC();
//        List<TOCReference> tocr = toc.getTocReferences();
//        StringBuilder tocs = new StringBuilder();
//        // String tocAsString = jBookToOpen.getBook().getContents().toString();
//
//        Iterator<TOCReference> it = tocr.iterator();
//        while (it.hasNext()) {
//            if (myJBookType.equals(JBookType.Unknown)) {
//                myJBookType = JBookType.ShortStory;
//            }
//            TOCReference tocRefLine = it.next();
//            String tocLine = tocRefLine.getTitle();
//            if (tocLine.toLowerCase().contains("chapter")) {
//                this.myJBookType = JBookType.Novel;
//            }
//            jChapter newChapter = new jChapter(tocLine);
//            this.chapters.add(newChapter);
//            int contIndex = chapters.size();
//            System.out.println("Chapter Index is: " + contIndex);
//            String chapCont = jBookToOpen.getChapter(contIndex);
//            newChapter.setChapterContents(chapCont);
//
//
////            System.out.println("TOC Line: " + tocLine );
//            if (tocLine != null) {
//                //  JLabel tocJLine = new JLabel(tocLine);
//                this.chapterListModel.addElement(tocLine);
//                // this.tocPanelList.add(tocJLine);
//                //JLabel tocLineLabel = new JLabel(tocLine.toString());
//                //this.tocTextArea.add( tocLineLabel);
//                // this.tocTextArea.append(tocLine);
//            }
//        }
//    }
//    private void openJBook( String JBookPath, JBook jBookToOpen )
    {
//        Icon coverIcon = null;
//        JLabel cover = new JLabel();
//        int frameW = this.coverPanel.getWidth();
//        int frameH = this.coverPanel.getHeight();
//        
//        this.clearJBookContents();
//        if (jBookToOpen != null && jBookToOpen.getJBookKey().hasTitle()) {
//            // System.out.println("Found Test Book, title is:  " + jBookToOpen.getTitle());
//            // this.getFrame().setTitle(jBookToOpen.getTitle());
//            setFrameTitle( jBookToOpen );
//            Resource imageResource = jBookToOpen.getCover();
//            InputStream imageIS;
//            try {
//                imageIS = imageResource.getInputStream();
//                coverIcon = createImageIcon(imageIS, JBookPath, frameW, frameH );
//
//            } catch (IOException ex) {
//                // ex.printStackTrace();
//            }
//
//            if (coverIcon != null) {
//                coverPanel.removeAll();
//                cover.setIcon(coverIcon);
//                cover.setBackground(Color.BLACK);
//                this.coverPanel.add(cover );
//            }
//        }
//        setInfo(jBookToOpen);
//        // clearJBookContents();
//        try {
//            createTOC(jBookToOpen);
//        } catch (IOException ex) {
//            // TODO Log something
////            ex.printStackTrace();
//        }
//        // clearJBookContents();
//        this.buildJBookContents();
//        this.jBookContents.setSelectedIndex(0);
//        this.setStatus("JBook Loaded !");
    }

    public JBookView(SingleFrameApplication app) {
        super(app);
        initComponents();
        JFrame parentFrame = getFrame();

        parentFrame.setPreferredSize(frameDim);
        coverPanel.setPreferredSize(coverDim);





        //<editor-fold defaultstate="collapsed" desc="comment">
        this.jChapterList.setModel(chapterListModel);
        //String testBook1 = "H:\\Books\\test.epub";
        String libPath = "C:\\Users\\Wintermut3\\Documents\\Books";
        String libSection = "\\18Days\\";
        String bookName = "18days_issue1.epub";
        StringBuilder sb = new StringBuilder(libPath);
        sb.append(libSection).append(bookName);
        
        Path bookPath = Paths.get( sb.toString() );
        String testBook1 = "18days_issue1.epub";
        testBook1 = "c:\\books\\vernejuletext98milnd11epub.epub";
        this.openJBook(bookPath.toString());
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                switch (propertyName) {
                    case "started":
                        if (!busyIconTimer.isRunning()) {
                            statusAnimationLabel.setIcon(busyIcons[0]);
                            busyIconIndex = 0;
                            busyIconTimer.start();
                        }
                        progressBar.setVisible(true);
                        progressBar.setIndeterminate(true);
                        break;
                    case "done":
                        busyIconTimer.stop();
                        statusAnimationLabel.setIcon(idleIcon);
                        progressBar.setVisible(false);
                        progressBar.setValue(0);
                        break;
                    case "message":
                        String text = (String) (evt.getNewValue());
                        statusMessageLabel.setText((text == null) ? "" : text);
                        messageTimer.restart();
                        break;
                    case "progress":
                        int value = (Integer) (evt.getNewValue());
                        progressBar.setVisible(true);
                        progressBar.setIndeterminate(false);
                        progressBar.setValue(value);
                        break;
                }
            }
        });
    }

    static ImageIcon createImageIcon(InputStream inStream, String iconName, int width, int height) {
        ImageIcon result = null;
        String fullIconPath = "/viewer/icons/" + iconName + ".png";
        try {

            // Image image = ImageIO.read(JBookView.class.getResourceAsStream(fullIconPath));
            Image image = ImageIO.read(inStream);
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            result = new ImageIcon(scaledImage);

        } catch (Exception e) {
            log.error("Icon \'" + fullIconPath + "\' not found");
        }
        return result;
    }

    private void setInfo(JBook bookToUse) {
        if (bookToUse == null) {
            setFrameTitle(bookToUse);
            return;
        }
        String title = bookToUse.getTitle();

        List<Author> authors = bookToUse.getAuthors();

        this.titleLabel.setText("Title: " + title);
        if (authors.size() == 1) {
            Author authToShow = authors.get(0);

            StringBuilder authList = new StringBuilder(authToShow.getFirstname());
            authList.append(" ").append(authToShow.getLastname());
            this.authorLabel.setText("Author: " + authList);
        }

        this.descriptionTextField.setText("");

        Iterator<String> it = bookToUse.getDescriptions().iterator();
        // StringBuilder descriptionLines = new StringBuilder();
        if (it != null && it.hasNext() ) {
            String descriptionLine = it.next();
            if (descriptionLine != null) {
                // descriptionLines.append(descriptionLine).append("\n");
                this.descriptionTextField.append(descriptionLine);
            }
        }
        // this.descriptionTextField.setText(descriptionLines.toString());
    }

    @Action
    public void showEbookChooser() {
        JFrame mainFrame = JBookApp.getApplication().getMainFrame();
        int x = mainFrame.getX();
        int y = mainFrame.getY();
        // File previousDir = null;


        if (previousDir == null) {
            previousDir = new File(this.bookPath);
        }
        ebookChooser.setSelectedFile(previousDir);

        int returnVal = ebookChooser.showOpenDialog(mainFrame);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File selectedFile = ebookChooser.getSelectedFile();
        if (selectedFile == null) {
            return;
        }
        if (!selectedFile.isDirectory()) {
            previousDir = selectedFile.getParentFile();
        }
        try {
            System.out.println("Selected File is: " + selectedFile.toString());
            this.setStatus("Loading JBook");
            this.openJBook(selectedFile.getPath());
            // Book book = (new EpubReader()).readEpub(new FileInputStream(selectedFile));
            // gotoBook(book);
        } catch (Exception e1) {
            log.error(e1.getMessage(), e1);
        }
        ebookChooser.setLocation(x, y);

        ebookChooser.setVisible(true);
    }

    public void setStatus(String newStatus) {
        this.statusMessageLabel.setText(newStatus);
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = JBookApp.getApplication().getMainFrame();
            aboutBox = new JBookAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        JBookApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jBookContents = new javax.swing.JTabbedPane();
        coverPanel = new javax.swing.JPanel();
        InfoPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        authorLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionTextField = new javax.swing.JTextArea();
        tocPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tocPanelList = new javax.swing.JPanel();
        jChapterListScroll = new javax.swing.JScrollPane();
        jChapterList = new javax.swing.JList();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        ebookOpenItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        ebookChooser = new javax.swing.JFileChooser();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.GridLayout(1, 0));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.wms.jbook.JBookApp.class).getContext().getResourceMap(JBookView.class);
        jBookContents.setBackground(resourceMap.getColor("jBookContents.background")); // NOI18N
        jBookContents.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jBookContents.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
        jBookContents.setName("jBookContents"); // NOI18N

        coverPanel.setBackground(resourceMap.getColor("coverPanel.background")); // NOI18N
        coverPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        coverPanel.setFont(resourceMap.getFont("coverPanel.font")); // NOI18N
        coverPanel.setName("coverPanel"); // NOI18N
        coverPanel.setLayout(new java.awt.GridLayout(1, 0));
        jBookContents.addTab(resourceMap.getString("coverPanel.TabConstraints.tabTitle"), coverPanel); // NOI18N

        InfoPanel.setBackground(resourceMap.getColor("InfoPanel.background")); // NOI18N
        InfoPanel.setForeground(resourceMap.getColor("InfoPanel.foreground")); // NOI18N
        InfoPanel.setName("InfoPanel"); // NOI18N

        titleLabel.setFont(resourceMap.getFont("titleLabel.font")); // NOI18N
        titleLabel.setForeground(InfoPanel.getForeground());
        titleLabel.setText(resourceMap.getString("titleLabel.text")); // NOI18N
        titleLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        titleLabel.setName("titleLabel"); // NOI18N

        authorLabel.setFont(resourceMap.getFont("authorLabel.font")); // NOI18N
        authorLabel.setForeground(InfoPanel.getForeground());
        authorLabel.setText(resourceMap.getString("authorLabel.text")); // NOI18N
        authorLabel.setName("authorLabel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        descriptionTextField.setColumns(20);
        descriptionTextField.setEditable(false);
        descriptionTextField.setLineWrap(true);
        descriptionTextField.setRows(5);
        descriptionTextField.setWrapStyleWord(true);
        descriptionTextField.setName("descriptionTextField"); // NOI18N
        jScrollPane1.setViewportView(descriptionTextField);

        javax.swing.GroupLayout InfoPanelLayout = new javax.swing.GroupLayout(InfoPanel);
        InfoPanel.setLayout(InfoPanelLayout);
        InfoPanelLayout.setHorizontalGroup(
            InfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(InfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                    .addComponent(authorLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(InfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(InfoPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        InfoPanelLayout.setVerticalGroup(
            InfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InfoPanelLayout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(authorLabel)
                .addGap(102, 102, 102)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(62, Short.MAX_VALUE))
            .addGroup(InfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(InfoPanelLayout.createSequentialGroup()
                    .addGap(44, 44, 44)
                    .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(468, Short.MAX_VALUE)))
        );

        jBookContents.addTab(resourceMap.getString("InfoPanel.TabConstraints.tabTitle"), InfoPanel); // NOI18N

        tocPanel.setName("tocPanel"); // NOI18N
        tocPanel.setLayout(new java.awt.GridLayout(1, 0));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tocPanelList.setName("tocPanelList"); // NOI18N
        tocPanelList.setLayout(new java.awt.GridLayout(1, 0));

        jChapterListScroll.setName("jChapterListScroll"); // NOI18N

        jChapterList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jChapterList.setName("jChapterList"); // NOI18N
        jChapterListScroll.setViewportView(jChapterList);

        tocPanelList.add(jChapterListScroll);

        jScrollPane2.setViewportView(tocPanelList);

        tocPanel.add(jScrollPane2);

        jBookContents.addTab(resourceMap.getString("tocPanel.TabConstraints.tabTitle"), tocPanel); // NOI18N

        mainPanel.add(jBookContents);

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.wms.jbook.JBookApp.class).getContext().getActionMap(JBookView.class, this);
        ebookOpenItem.setAction(actionMap.get("showEbookChooser")); // NOI18N
        ebookOpenItem.setText(resourceMap.getString("ebookOpenItem.text")); // NOI18N
        ebookOpenItem.setName("ebookOpenItem"); // NOI18N
        fileMenu.add(ebookOpenItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        ebookChooser.setName("ebookChooser"); // NOI18N
        ebookChooser.setOpaque(true);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel InfoPanel;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JPanel coverPanel;
    private javax.swing.JTextArea descriptionTextField;
    private javax.swing.JFileChooser ebookChooser;
    private javax.swing.JMenuItem ebookOpenItem;
    private javax.swing.JTabbedPane jBookContents;
    private javax.swing.JList jChapterList;
    private javax.swing.JScrollPane jChapterListScroll;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel tocPanel;
    private javax.swing.JPanel tocPanelList;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    private ArrayList<jChapter> chapters = new ArrayList<>();
    private DefaultListModel<String> chapterListModel = new DefaultListModel<>();
}

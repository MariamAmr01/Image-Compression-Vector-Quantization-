import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

class OpenFileAction extends AbstractAction {
    JFrame frame;
    JFileChooser chooser;
    File file;

    OpenFileAction(JFrame frame, JFileChooser chooser) {
        super("Open...");
        this.chooser = chooser;
        this.frame = frame;
    }

    public void actionPerformed(ActionEvent evt) {
        // Show dialog; this method does not return until dialog is closed
        chooser.showOpenDialog(frame);

        // Get the selected file
        file = chooser.getSelectedFile();
    }

};
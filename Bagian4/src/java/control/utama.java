/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
/**
 *
 * @author Hewlett-Packard
 */
public class utama extends GenericForwardComposer{
     private Label       lblUser;
    private Window      win;
  
    public void onCreate$win()
    {
        if (Sessions.getCurrent().getAttribute("userid") == null)
        {
            win.detach();
            win = null;
            execution.sendRedirect("../index.zul", "_top");
            return;
        }
  lblUser.setValue(session.getAttribute("userid").toString());
    }
  
    public void onClick$mnLogout()throws InterruptedException
    {
        Messagebox.show("Terimakasih anda telah keluar dengan benar", "Informasi", Messagebox.OK, Messagebox.INFORMATION); 
        session.invalidate(); 
        execution.sendRedirect("index.zul", "_top");
    }
}


/*
 * Penjelasan:
1. Pada "Sources Package/control/utama.java" terdapat

    if (Sessions.getCurrent().getAttribute("userid") == null)

yang berfungsi untuk melakukan pengecekan apakah halaman tersebut dibuka setelah login atau tidak.
2. Pada "Sources Package/control/utama.java" terdapat

    session.invalidate();

yang berfungsi untuk menghapus sesi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;
import dao.Pengguna;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import objeck.md5;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Captcha;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
/**
 *
 * @author Hewlett-Packard
 */
public class index extends GenericForwardComposer
{
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("dao-persistence-pfmPU");
    static EntityManager emTabel    = emf.createEntityManager();

    private Textbox     txtboxUserID,txtboxPassword;
    private Captcha     cLogin;
    private Textbox     txtCaptcha;

    public void onCreate$win()
    {
        txtboxUserID.setFocus(true);
    }

    public void onClick$btnLogin()throws InterruptedException, NoSuchAlgorithmException, NoSuchAlgorithmException, UnsupportedEncodingException
    {
        if ((txtboxUserID.getValue().equals("")) || (txtboxPassword.getValue().equals("")))
        {
            Messagebox.show("User ID dan password tidak boleh kosong.", "Informasi", Messagebox.OK, Messagebox.INFORMATION);
            txtboxUserID.setFocus(true);
            return;
        }
        else if(!cLogin.getValue().equalsIgnoreCase(txtCaptcha.getValue()))
        {
            Messagebox.show("Captcha harus sama dengan yang muncul pada gambar di sebelah kiri", "Informasi", Messagebox.OK, Messagebox.INFORMATION);
            txtCaptcha.setFocus(true);
            txtCaptcha.setSelectionRange(0, 3);
            return;
        }
        else
        {
            listUser        = emTabel.createNamedQuery("Pengguna.findByNamaloginKatasandi")
                                .setHint("eclipselink.refresh", "true")
                                .setParameter("namalogin", txtboxUserID.getValue())
                                .setParameter("katasandi", md5.decode(txtboxPassword.getValue()))
                                .getResultList();
            if (listUser.isEmpty())
            {
                Messagebox.show("Pengguna " + txtboxUserID.getValue() + " belum terdaftar atau kata sandi salah", "Informasi", Messagebox.OK, Messagebox.INFORMATION);
                txtboxUserID.setFocus(true);
                return;
            }
        }
        session.setAttribute("userid", txtboxUserID.getValue());
        session.setAttribute("id", ((Pengguna) listUser.get(0)).getIdpengguna());
        Executions.sendRedirect("utama.zul");
    }

    //Zul Object
    protected List  listUser;
}

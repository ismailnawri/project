/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import dao.VRekap;
import formater.tipedata;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.Jasperreport;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author Hewlett-Packard
 */
public class rekap extends GenericForwardComposer
 {
    static EntityManagerFactory emf     = Persistence.createEntityManagerFactory("dao-persistence-pfmPU");
    static EntityManager        emTabel = emf.createEntityManager();
    private List                dataList;
    private Jasperreport        laporan;
    private Map                 reportParam;
    protected   Window          win;
    protected   Intbox          intMulai, intAkhir;
    protected   Textbox         txtTahun;
    private     String          mode = "";
    public rekap()
    {
    }
    public void onCreate$win()
    {
        if (Sessions.getCurrent().getAttribute("userid") == null)
        {
            win.detach();
            win = null;
            execution.sendRedirect("../index.zul", "_top");
            return;
        }
        intMulai.setFocus(true);
    }
    public void onClick$btnTampil1() throws Exception
    {
        mode = "diantara";
        tampilkanlaporan();
    }
    public void onClick$btnTampil2() throws Exception
    {
        mode = "dalam";
        tampilkanlaporan();
    }
   
    //Laporan
    public class SumberDataLaporan implements JRDataSource
    {
        private Object[][] data;
        private int index = -1;
        public SumberDataLaporan() throws Exception
        {
            data = setDataToReport();
        }
        @Override
        public boolean next() throws JRException
        {
            index++;
            return (index < data.length);
        }
        @Override
        public Object getFieldValue(JRField field) throws JRException
        {
            Object value = null;
            LinkedList llBuff   = getField();
            for (int i=0; i<llBuff.size(); i++)
                if (llBuff.get(i).equals(field.getName()))
                {
                    value = data[index][i];
                    i = llBuff.size();
                }
            return value;
        }
    }
    public Object[][] setDataToReport() throws Exception
    {
        VRekap  buffer_objek;
        if (mode.equals("diantara"))
            dataList = emTabel.createNamedQuery("VRekap.findByTahunBetween")
                                .setHint("eclipselink.refresh", "true")
                                .setParameter("tahun1", intMulai.getValue())
                                .setParameter("tahun2", intAkhir.getValue())
                                .setParameter("idpengguna", tipedata.keInt(Sessions.getCurrent().getAttribute("id")))
                                .getResultList();
        else
        {
            LinkedList<Integer> periode = new LinkedList<Integer>();
            StringTokenizer st = new StringTokenizer(txtTahun.getValue(),",");
            while(st.hasMoreTokens())
                periode.add(tipedata.keInt(st.nextElement().toString().trim()));
            dataList = emTabel.createNamedQuery("VRekap.findByTahunIn")
                                .setHint("eclipselink.refresh", "true")
                                .setParameter("tahun", periode)
                                .setParameter("idpengguna", tipedata.keInt(Sessions.getCurrent().getAttribute("id")))
                                .getResultList();
        }
        Object[][] xBuffer = new Object[dataList.size()][3];
        int x = 0;
        for (Iterator it = dataList.iterator(); it.hasNext();)
        {
            buffer_objek = (VRekap) it.next();
            xBuffer[x][0]   = buffer_objek.getNamaitem();
            xBuffer[x][1]   = buffer_objek.getNominal();
            xBuffer[x][2]   = tipedata.keBoolean(buffer_objek.getAlurkas());
            x++;
        }
        dataList    = null;
        return xBuffer;
    }
    public LinkedList getField()
    {
        LinkedList ll = new LinkedList();
        ll.add("namaitem");
        ll.add("nominal");
        ll.add("alurkas");
        return ll;
    }
    public void tampilkanlaporan() throws Exception
    {
        laporan.setSrc("/laporan/rekap transaksi.jasper");
        reportParam = new HashMap();
        reportParam.put("namapengguna", Sessions.getCurrent().getAttribute("userid"));
        if (mode.equals("diantara"))
            reportParam.put("periode", intMulai.getValue() + " s.d. " + intAkhir.getValue());
        else
            reportParam.put("periode", txtTahun.getValue());
        laporan.setParameters(reportParam);
        laporan.setDatasource(new SumberDataLaporan());
    }

}

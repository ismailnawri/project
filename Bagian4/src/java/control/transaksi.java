/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;
import dao.Item;
import dao.Transaksi;
import formater.tipedata;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import objeck.tombolcrud;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
/**
 *
 * @author Hewlett-Packard
 */
public class transaksi extends GenericForwardComposer {
    private     tombolcrud perilakuTombolCrud = new tombolcrud();
    protected   Button btnBatal;
    protected   Button btnHapus;
    protected   Button btnSimpan;
    protected   Button btnTambah;

    public transaksi()
    {
    }
    
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("dao-persistence-pfmPU");
    static EntityManager emTabel    = emf.createEntityManager();
    private     objeck.transaksi trans;
    protected   Window  win;

    //Zul Action
    private void bindcrud()
    {
        listTransaksi       = emTabel.createNamedQuery("Transaksi.findAll")
                                .setHint("eclipselink.refresh", "true")
                                .setFirstResult(0)
                                .setMaxResults(20)
                                .getResultList();
        trans           = new objeck.transaksi();
        trans.setEm(emTabel);
    }

    private void reset()
    {
        perilakuTombolCrud.setTombolBatal(btnBatal);
        perilakuTombolCrud.setTombolHapus(btnHapus);
        perilakuTombolCrud.setTombolSimpan(btnSimpan);
        perilakuTombolCrud.setTombolTambah(btnTambah);
        perilakuTombolCrud.keadaanAwal();
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
        listItem    = emTabel.createNamedQuery("Item.findAll")
                                .setHint("eclipselink.refresh", "true")
                                .getResultList();
        bindcrud();
        reset();
        perilakuTombolCrud.setObjekFokus(cmbItem);
        perilakuTombolCrud.tambahObjekZul(cmbItem, "Item", true);
        perilakuTombolCrud.tambahObjekZul(dateTanggal, "Tanggal Transaksi", true);
        perilakuTombolCrud.tambahObjekZul(intNominal, "Nominal Transaksi", true);
        perilakuTombolCrud.tambahObjekZul(txtKeterangan, "Keterangan", true);
        perilakuTombolCrud.siapIsi(false);
    }

    public void onClick$btnTampilTransaksi()
    {
        listTransaksi          = emTabel.createNamedQuery("Transaksi.findByKeteranganLike")
                                    .setHint("eclipselink.refresh", "true")
                                    .setParameter("keterangan", "%" + txtCariTransaksi.getValue() + "%")
                                    .getResultList();
        objekBarisTransaksi    = new Transaksi();
    }

    public void onSelect$lstTransaksi()
    {
        perilakuTombolCrud.dataTerpilih();
        perilakuTombolCrud.siapIsi(true);
    }

    //Object Baris
    protected Transaksi objekBarisTransaksi;
    protected Item      objekBarisItem;

    //Zul Object
    protected List      listTransaksi;
    protected List      listItem;
    protected Textbox   txtCariTransaksi;
    protected Textbox   txtKeterangan;
    protected Intbox    intNominal;
    protected Datebox   dateTanggal;
    protected Combobox  cmbItem;
    protected Listbox   lstTransaksi;
 
    //Manipulasi Database
    public void onClick$btnTambah()
    {
        perilakuTombolCrud.tombolTambahKlik();
        Transaksi baru = new Transaksi();
        listTransaksi.add(baru);
        objekBarisTransaksi = (Transaksi) listTransaksi.get(listTransaksi.size()-1);
        objekBarisTransaksi.setIdpengguna(tipedata.keInt(Sessions.getCurrent().getAttribute("id")));
    }

    public void onClick$btnBatal()
    {
        perilakuTombolCrud.tombolBatalKlik();
        listTransaksi.remove(listTransaksi.size()-1);
        objekBarisTransaksi = new Transaksi();
    }

    public void onClick$btnSimpan() throws InterruptedException
    {
        if (perilakuTombolCrud.cekIsi())
        {
            trans.setErr(0);
            trans.setO(objekBarisTransaksi);
            trans.simpan();
            if (trans.getErr() == 0) perilakuTombolCrud.tombolSimpanKlik();
        }
    }

    public void onClick$btnHapus() throws InterruptedException
    {
        trans.setErr(0);
        trans.setO(objekBarisTransaksi);
        trans.hapus();
        if (trans.getErr() == 0)
        {
            listTransaksi.remove(lstTransaksi.getSelectedIndex());
            objekBarisTransaksi = new Transaksi();
            perilakuTombolCrud.tombolHapusKlik();
        }
    }
    
     //deklarasi
    //protected Transaksi objekBarisTransaksi;

    //getter dan setter
    public Transaksi getObjekBarisTransaksi()
    {
        return objekBarisTransaksi;
    }

    public Item getObjekBarisItem() {
        return objekBarisItem;
    }

    public void setObjekBarisItem(Item objekBarisItem) {
        this.objekBarisItem = objekBarisItem;
    }

    public List getListTransaksi() {
        return listTransaksi;
    }

    public void setListTransaksi(List listTransaksi) {
        this.listTransaksi = listTransaksi;
    }

    public List getListItem() {
        return listItem;
    }

    public void setListItem(List listItem) {
        this.listItem = listItem;
    }

    public void setObjekBarisTransaksi(Transaksi objekBarisTransaksi)
    {
        this.objekBarisTransaksi = objekBarisTransaksi;
    }
    
    
}

/*
 * Penjelasan:
1. Pada "Web Pages/transaksi.zul" terdapat baris  

    <textbox id="txtKeterangan" value="@{win$composer.objekBarisTransaksi.keterangan, load-after='lstTransaksi.onSelect, btnTampilTransaksi.onClick, btnTambah.onClick'}" width="500px" maxlength="100"/>

yang di dalamnya terdapat kode untuk berhubungan dengan "transaksi.java", yaitu: "@{win$composer.xxx}", dalam baris ini adalah "@{win$composer.objekBarisTransaksi}". Di dalam "transaksi.java" harus dideklarasikan dan dibuatkan method untuk getter dan setter-nya, seperti di bawah ini

    //deklarasi
    protected Transaksi objekBarisTransaksi;

    //getter dan setter
    public Transaksi getObjekBarisTransaksi()
    {
        return objekBarisTransaksi;
    }

    public void setObjekBarisTransaksi(Transaksi objekBarisTransaksi)
    {
        this.objekBarisTransaksi = objekBarisTransaksi;
    }

Oleh karena itu, pada "transaksi.java" perlu ditambahkan getter dan setter untuk 4 buah objek, yaitu: objekBarisTransaksi, 
* objekBarisItem, listItem, dan listTransaksi dengan cara menekan tombol "alt + insert", pilih "Getters and Setters...", 
* kemudian pilih keempat objek tersebut.
Pada baris tersebut juga terdapat "load-after" yang mengindikasikan bahwa textbox tersebut akan diisi oleh sebuah nilai 
* yang diambil dari "objekBarisTransaksi.keterangan" setelah "lstTransaksi.onSelect", "btnTampilTransaksi.onClick" atau
* "btnTambah.onClick".
 
2. Pada "Web Pages/transaksi.zul" terdapat baris

    <combobox id="cmbItem" model="@{win$composer.listItem, load-after='win.onCreate'}" value="@{win$composer.objekBarisTransaksi.iditem, converter='converter.combobox_value', load-after='lstTransaksi.onSelect, btnTampilTransaksi.onClick, btnTambah.onClick'}" autocomplete="true">
    <comboitem forEach="@{win$composer.listItem}" self="@{each='objekBarisItem'}" label="@{objekBarisItem.namaitem}" value="@{objekBarisItem.iditem}" />
    </combobox>

untuk mengisi dan mengkaitkan baris yang dipilih ke dalam combobox. Combobox "cmbItem" diisi dengan "listItem" yang ada 
* pada "transaksi.java" setelah jendela "win" dibuat (lihat pada "model"), kemudian masing-masing baris pada "listItem" 
* akan disimpan ke dalam "objekBarisItem". 
* Teks yang tampil pada combobox sesuai yang ada pada label combobox (dalam hal ini objekBarisItem.namaitem. 
* Nilai (tidak ditampilkan) pada combobox sesuai dengan yang ada pada value combobox (dalam hal ini "objekBarisItem.iditem"). 
* Secara bawaan, yang dikembalikan oleh combobox ke "objekBarisTransaksi.iditem" pada "transaksi.java" adalah labelnya. Namun, 
* untuk mengembalikan nilainya, maka diperlukan converter "converter.combobox_value". Pada converter tersebut akan menangkap 
* labelnya, kemudian dari label tersebut dicari nilainya dan nilai tersebut dikembalikan.
3. Pada "Web Pages/transaksi.zul" terdapat baris

    <datebox id="dateTanggal" value="@{win$composer.objekBarisTransaksi.tgltransaksi, load-after='lstTransaksi.onSelect, btnTampilTransaksi.onClick, btnTambah.onClick', converter='converter.tanggal_date'}"  format="dd-MM-yyyy" width="500px"/>

untuk mengisi dan mengkaitkan baris yang dipilih ke dalam datebox. Teks yang ditampilkan pada datebox "dateTanggal" diganti formatnya menjadi tanggal-bulan-tahun. Nilai yang diisikan diluar format tersebut dianggap tidak valid. Untuk mengembalikan nilai dengan benar, datebox perlu menggunakan converter "converter.tanggal_date" sehingga nilai yang dikembalikan memiliki tipe data Date, bukan String.
4. Pada "Web Pages/transaksi.zul" terdapat baris

    <listbox id="lstTransaksi" rows="10" mold="paging" pageSize="10" pagingPosition="top" style="margin-top:5px" model="@{win$composer.listTransaksi, load-after='win.onCreate, btnTampilTransaksi.onClick, btnSimpan.onClick, btnTambah.onClick, btnBatal.onClick'}" selectedItem="@{win$composer.objekBarisTransaksi}">
    ...
    <listitem forEach="@{win$composer.listTransaksi}" self="@{each='objekBarisTransaksi'}" value="@{objekBarisTransaksi}" >
    <listcell label="@{objekBarisTransaksi.idtransaksi}"/>
    ...
    </listitem>
    </listbox>

untuk mengisi dan mengkaitkan baris yang dipilih ke dalam listbox. Listbox "lstTransaksi" diisi dengan "listTransaksi" yang ada pada "transaksi.java" setelah aksi-aksi pada "load-after" terjadi (lihat pada "model"), kemudian masing-masing baris pada "listTransaksi" akan disimpan ke dalam "objekBarisTransaksi". Masing-masing baris bernilai objek, dalam hal ini "objekBarisTransaksi". Untuk menampilkannya dapat dilakukan melalui listcell. Jadi, meskipun yang ditampilkan hanya satu kolom saja, tetapi nilai yang dikembalikan nanti adalah objek, tidak hanya satu kolom tersebut. Agar listbox menampilkan per halaman, maka harus disertakan "mold" (harus "paging"), "rows" dan "pageSize". "pagingPosition" bersifat pilihan. Apabila salah satu baris terpilih, maka listbox akan mengirimkan nilai (dalam bentuk objek) ke "transaksi.java" (lihat "selectedItem"). Setelah itu, kontrol yang menggunakan "lstTransaksi.onSelect" pada "load-after" akan menampilkan sesuai yang tercantum di dalam "value"-nya, misalnya: "intNominal" yang pada "value"-nya tertulis "@{win$composer.objekBarisTransaksi.nominal}" akan menampilkan nominal transaksi.
5. Pada "Sources Packages/transaksi.java" terdapat baris

    listTransaksi = emTabel.createNamedQuery( "Transaksi.findAll" )
              .setHint("eclipselink.refresh", "true")
              .setFirstResult(0)
              .setMaxResults(20)
              .getResultList();

untuk menampilkan hasil dimulai dari "FirstResult", sebanyak "MaxResults" baris. Baris ini untuk mengganti "limit" yang tidak dapat digunakan pada permintaan bernama, karena bentuk "select t from transaksi t where t.keterangan = :keterangan limit 0, 20" tidak diperbolehkan.
6. Pada "Sources Packages/transaksi.java" terdapat baris

    listTransaksi = emTabel.createNamedQuery( "Transaksi.findByKeteranganLike" )
           .setHint("eclipselink.refresh", "true")
           .setParameter("keterangan", "%" + txtCariTransaksi.getValue() + "%")
           .getResultList();

untuk menampilkan hasil yang sesuai dengan pola. Pada parameter "keterangan" menggunakan pola %nilai%. Baris ini untuk menangani "like" yang tidak dapat langsung digunakan pada permintaan bernama, karena bentuk "select t from transaksi t where t.keterangan = '%:keterangan%'" tidak diperbolehkan.
7. Pada "Sources Packages/transaksi.java" terdapat method "reset" yang di dalamnya terdapat kode untuk mendaftarkan tombol "btnBatal", "btnTambah", "btnSimpan", dan "btnHapus" ke objek perilakuTombolCrud dan pada method "onCreate$win" terdapat kode untuk mendaftarkan kontrol-kontrol lain ke objek perilakuTombolCrud, sehingga akan meringkas penulisan kode dan mempermudah pengaturan. Kemudahan tersebut dijumpai pada "reset", "onCreate$win", "onSelect$lstTransaksi", "onClick$btnTambah", "onClick$btnBatal", "onClick$btnHapus", dan "onClick$btnSimpan".
8. Untuk melakukan proses create-update-delete, pola yang ada pada method "onClick$btnTambah", "onClick$btnBatal", "onClick$btnHapus", dan "onClick$btnSimpan" sudah tepat, hanya saja pada method "onClick$btnTambah" ditambahkan

    objekBarisTransaksi.setIdpengguna(tipedata.keInt(Sessions.getCurrent().getAttribute("id")));

karena pada formulir tidak membutuhkan masukan "idpengguna". "idpengguna" didapatkan dari Session pada waktu pengguna masuk ke dalam sistem. Oleh karena itu, "idpengguna" perlu diset dari method "onClick$btnTambah".
 */

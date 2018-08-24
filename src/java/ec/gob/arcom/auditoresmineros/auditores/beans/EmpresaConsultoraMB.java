/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.auditores.beans;

import ec.gob.arcom.auditoresmineros.catalogos.Catalogo;
import ec.gob.arcom.auditoresmineros.controllers.LocalidadController;
import ec.gob.arcom.auditoresmineros.keygen.Keygen;
import ec.gob.arcom.auditoresmineros.mail.FromTo;
import ec.gob.arcom.auditoresmineros.mail.MailSender;
import ec.gob.arcom.auditoresmineros.persistencia.daos.AuditorSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.CatalogoSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.EmpresaConsultoraSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.LocalidadSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Auditor;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Colaborador;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Direccion;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.EmpresaConsultoraAuditoria;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Localidad;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Pago;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.TipoAuditor;
import ec.gob.arcom.auditoresmineros.util.Crypt;
import ec.gob.arcom.auditoresmineros.util.FacesUtilComun;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Will
 */
@Named(value = "empresaConsultoraMB")
@SessionScoped
public class EmpresaConsultoraMB implements Serializable {
    @EJB
    private EmpresaConsultoraSBLocal empresaConsultoraSB;
    @EJB
    private LocalidadSBLocal localidadSB;
    @EJB
    private AuditorSBLocal auditorSB;
    @EJB
    private CatalogoSBLocal catalogoSB;
    
    /*
    * Propiedades
    */
    private EmpresaConsultoraAuditoria empresaConsultoraAuditoria;
    //Cargar las provincias
    private List<Localidad> provincias;
    //Para subir archivos
    private UploadedFile upFile;
    private boolean showUploaded=true;
    private boolean showButtons=false;
    private Date fechaMaxPago;
    private String nombreEntidadFinanciera;
    private Catalogo selectedTipoAuditorValue;
    private boolean rucValido= false;
    private Pago pago;
    
    @Inject
    private AdjuntoMB adjuntoMB;
    
    /**
     * Creates a new instance of EmpresaConsultoraMB
     */
    public EmpresaConsultoraMB() {
    }
    
    @PostConstruct
    private void init() {
        this.empresaConsultoraAuditoria= new EmpresaConsultoraAuditoria();
        this.empresaConsultoraAuditoria.setDireccion(new Direccion());
        this.empresaConsultoraAuditoria.setActivo(true);
        this.empresaConsultoraAuditoria.setEstado(catalogoSB.findByNemonico(Auditor.TRAMITE));
        this.empresaConsultoraAuditoria.setAniosExperiencia(5);
        this.colaborador= new Colaborador();
        this.colaborador.setAniosExperiencia(1);
        this.pago= new Pago();
        this.empresaConsultoraAuditoria.setPagos(new ArrayList<>());
        this.empresaConsultoraAuditoria.setColaboradores(new ArrayList<>());
        this.empresaConsultoraAuditoria.setTiposAuditor(new ArrayList<>());
    }
    
    /*
     * Getters and Setters
     */
    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }
    
    public AdjuntoMB getAdjuntoMB() {
        return adjuntoMB;
    }

    public void setAdjuntoMB(AdjuntoMB adjuntoMB) {
        this.adjuntoMB = adjuntoMB;
    }
    
    public Catalogo getSelectedTipoAuditorValue() {
        return selectedTipoAuditorValue;
    }
    
    public void setSelectedTipoAuditorValue(Catalogo selectedTipoAuditorValue) {
        this.selectedTipoAuditorValue = selectedTipoAuditorValue;
    }

    public EmpresaConsultoraAuditoria getEmpresaConsultoraAuditoria() {
        return empresaConsultoraAuditoria;
    }

    public void setEmpresaConsultoraAuditoria(EmpresaConsultoraAuditoria empresaConsultoraAuditoria) {
        this.empresaConsultoraAuditoria = empresaConsultoraAuditoria;
    }

    public List<Localidad> getProvincias() {
        this.provincias= LocalidadController.getProvincias(localidadSB);
        return provincias;
    }

    public void setProvincias(List<Localidad> provincias) {
        this.provincias = provincias;
    }

    public UploadedFile getUpFile() {
        return upFile;
    }

    public void setUpFile(UploadedFile upFile) {
        this.upFile = upFile;
    }

    public boolean isShowUploaded() {
        return showUploaded;
    }

    public void setShowUploaded(boolean showUploaded) {
        this.showUploaded = showUploaded;
    }

    public boolean isShowButtons() {
        return showButtons;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
    }

    public Date getFechaMaxPago() {
        fechaMaxPago= Calendar.getInstance().getTime();
        return fechaMaxPago;
    }

    public void setFechaMaxPago(Date fechaMaxPago) {
        this.fechaMaxPago = fechaMaxPago;
    }

    public String getNombreEntidadFinanciera() {
        nombreEntidadFinanciera= "BANCO PICHINCHA";
        return nombreEntidadFinanciera;
    }

    public void setNombreEntidadFinanciera(String nombreEntidadFinanciera) {
        this.nombreEntidadFinanciera = nombreEntidadFinanciera;
    }
    
    /*
    * Metodos
    */
    public void newECAAction() {
        System.out.println("Nuevo EAC action");
        init();
    }
    
    public void sendMailAction(String key) {
        FromTo ft= new FromTo();
        MailSender ms= new MailSender();
        ft.setTo(this.empresaConsultoraAuditoria.getEmail());
        ft.setSubject("Info Auditores Técnicos Mineros - Registro Exitoso");
        ft.setMsg(ms.getCuentaCreadaMsg(this.empresaConsultoraAuditoria.getRuc(), key));
        ms.sendMailHTML(ft);
    }
    
    public boolean saveAction() {
        System.out.println("Selected tipo de auditor: " + selectedTipoAuditorValue);
        System.out.println("Ruc: " + empresaConsultoraAuditoria.getRuc());
        if(adjuntoMB.subirArchivos(empresaConsultoraAuditoria.getRuc())) {
            try {
                this.empresaConsultoraAuditoria.setFechaDeRegistro(Calendar.getInstance().getTime());
                this.empresaConsultoraAuditoria.setHoraDeRegistro(Calendar.getInstance().getTime());
                this.empresaConsultoraAuditoria.getTiposAuditor().add(generarTipoAuditor(selectedTipoAuditorValue));
                String key= Keygen.getPassword();
                this.empresaConsultoraAuditoria.setPasswd(Crypt.cryptMD5(key));
                this.empresaConsultoraSB.saveECA(empresaConsultoraAuditoria);
                sendMailAction(key);
                FacesUtilComun.showInfoMessage("Registro correcto", "Su clave ha sido enviada a su email");
                return true;
            } catch(Exception ex) {
                FacesUtilComun.showErrorMessage("Error", "Se presento un error al guardar los datos");
                System.out.println(ex.toString());
            }
        }
        return false;
    }
        
    public String cancelAction() {
        this.resetAll();
        return "login";
    }
    
    private void resetAll() {
        HttpSession session = FacesUtilComun.getSession();
        session.invalidate();
    }
    
    /*
    * Agregar colaboradores
    */
    private Colaborador colaborador;

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }
    
    public void addColaborador() {
        if(comprobarColaborador(colaborador)) {
            List tmp01= this.empresaConsultoraAuditoria.getColaboradores();
            tmp01.add(this.colaborador);
            this.empresaConsultoraAuditoria.setColaboradores(tmp01);
            this.colaborador= new Colaborador();
            this.colaborador.setAniosExperiencia(1);
        } else {
            FacesUtilComun.showErrorMessage("Error", "Datos no válidos");
            FacesUtilComun.updateComponent("growl");
        }
    }
    public void removeColaborador(Colaborador colaborador) {
        List tmp01= this.empresaConsultoraAuditoria.getColaboradores();
        tmp01.remove(colaborador);
        this.empresaConsultoraAuditoria.setColaboradores(tmp01);
    }
    public void limpiarCampos() {
        this.colaborador= new Colaborador();
        this.colaborador.setAniosExperiencia(5);
    }
    
    public void verificarRucRepetido(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {
        Auditor tmp= auditorSB.findByRuc(arg2.toString());
        if(tmp!=null) {
            rucValido= false;
            FacesUtilComun.showErrorMessage("Error", "El RUC ingresado ya se encuentra registrado");
            FacesUtilComun.updateComponent("form");
        } else {
            rucValido= true;
        }
    }
    
    public String goToColaboradores() {
        if(rucValido) {
            if(empresaConsultoraAuditoria.getPagos().size()>0) {
                return "agregarcolaboradores";
            } else {
                FacesUtilComun.showErrorMessage("Error", "Debe agregar almenos un pago");
            }
        }
        return null;
    }
    
    public String goToAdjuntos() {
        if(this.empresaConsultoraAuditoria.getColaboradores().size()>0) {
            adjuntoMB.setUpFileList(new ArrayList<UploadedFile>());
            return "cargaradjuntospj";
        } else {
            FacesUtilComun.showErrorMessage("Error", "Debe agregar almenos un colaborador");
        }
        return null;
    }
    
    public String endAction() {
        if(saveAction()) {
            this.resetAll();
            return "login";
        }
        return null;
    }
    
    private boolean comprobarColaborador(Colaborador clb) {
        boolean good=false;
        if(clb.getCedula().length()==10 && clb.getNombre().length()>0 && clb.getApellido().length()>0 && 
                clb.getTitulo().length()>0 && clb.getRegSenescyt().length()>0 && comprobarClbRepetido(clb)) {
            
            good= true;
        }
        return good;
    }
    
    private boolean comprobarClbRepetido(Colaborador clb) {
        boolean good=true;
        for (Colaborador col : this.empresaConsultoraAuditoria.getColaboradores()) {
            if(clb.getCedula().equals(col.getCedula())) {
                good=false;
            }
        }
        return good;
    }
    
    /*
    * Agregar pagos a la lista
    */
    public void addPago() {
        if(pago.getNumeroDeposito().length()>0 && pago.getValorDeposito()>0 && pago.getFechaDeposito().toString().length()>0) {
            if(!comprobarPagoRepetido(pago)) {
                pago.setEntidadFinanciera(nombreEntidadFinanciera);
                empresaConsultoraAuditoria.getPagos().add(pago);
                pago= new Pago();
            } else {
                FacesUtilComun.showErrorMessage("Error", "Ya ha sido agregado este número de pago");
            }
        } else {
            FacesUtilComun.showErrorMessage("Error", "Debe llenar todos los campos para agregar un pago");
        }
    }
    public void removePago(Pago p) {
        empresaConsultoraAuditoria.getPagos().remove(p);
    }
    
    private boolean comprobarPagoRepetido(Pago p) {
        for (Pago pago1 : empresaConsultoraAuditoria.getPagos()) {
            if(pago1.getNumeroDeposito().equals(pago.getNumeroDeposito())) {
                return true;
            }
        }
        return false;
    }
    
    public TipoAuditor generarTipoAuditor(Catalogo c) {
        TipoAuditor tipo= new TipoAuditor();
        tipo.setCatalogo(c);
        return tipo;
    }
}

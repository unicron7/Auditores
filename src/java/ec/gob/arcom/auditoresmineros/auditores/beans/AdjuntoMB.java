/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.auditores.beans;

import ec.gob.arcom.auditoresmineros.controllers.FileUploadController;
import ec.gob.arcom.auditoresmineros.util.FacesUtilComun;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author mejiaw
 */
@Named(value = "adjuntoMB")
@SessionScoped
public class AdjuntoMB implements Serializable {
    private UploadedFile upFile;
    private List<UploadedFile> upFileList;
    
    /**
     * Creates a new instance of AdjuntoMB
     */
    public AdjuntoMB() {
        upFileList= new ArrayList<>();
    }

    public UploadedFile getUpFile() {
        return upFile;
    }

    public void setUpFile(UploadedFile upFile) {
        this.upFile = upFile;
    }

    public List<UploadedFile> getUpFileList() {
        return upFileList;
    }

    public void setUpFileList(List<UploadedFile> upFileList) {
        this.upFileList = upFileList;
    }
    
    public void addArchivos(FileUploadEvent event) {
        boolean existe= false;
        for(UploadedFile f : upFileList) {
            if(f.getFileName().equals(event.getFile().getFileName())) {
                existe= true;
            }
        }
        
        if(upFileList.size()<5) {
            if(!existe) {
                upFileList.add(event.getFile());
            } else {
                FacesUtilComun.showErrorMessage("Error", "Este archivo ya ha sido agregado");
            }
        } else {
            FacesUtilComun.showErrorMessage("Error", "Solo puede subir 5 archivos");
        }
        RequestContext.getCurrentInstance().execute("PF('archivosDlgWg').hide();");
    }
    
    /*
    public void addFile() {
        if(upFile!=null && upFile.getSize()>0 && upFile.getSize()<=8388608 && 
                FileUploadController.verificarTipoArchivo(FileUploadController.obtenerExtArchivo(upFile.getFileName())) && 
                !FileUploadController.verificarArchivoRepetido(upFile.getFileName(), upFileList)) {
            this.upFileList.add(upFile);
        } else if(upFile.getSize()==0) {
            FacesUtilComun.showErrorMessage("Error", "No selecciono ningun archivo");
        } else if(!FileUploadController.verificarTipoArchivo(FileUploadController.obtenerExtArchivo(upFile.getFileName()))) {
            FacesUtilComun.showErrorMessage("Error", "No se admite este tipo de archivo");
        } else if(FileUploadController.verificarArchivoRepetido(upFile.getFileName(), upFileList)) {
            FacesUtilComun.showErrorMessage("Error", "Este archivo ya ha sido agregado");
        } else {
            FacesUtilComun.showErrorMessage("Error", "El archivo excede el tamaño permitido");
        }
    }
    */

    public String sizeMB(long size) {
        return FileUploadController.getSizeMB(size);
    }
    
    public void removeFile(UploadedFile file) {
        this.upFileList.remove(file);
    }
    
    public boolean subirArchivos(String ruc) {
        if(upFileList.size()>0) {
            if(guardarArchivos(ruc)==1) {
                upFileList.removeAll(upFileList);
                FacesUtilComun.showInfoMessage("Aviso", "Archivos enviados correctamente");
                return true;
            } else {
                FacesUtilComun.showErrorMessage("Error", "Ocurrio un error al cargar los archivos");
            }
        } else {
            FacesUtilComun.showErrorMessage("", "Debe adjuntar almenos un archivo");
        }
        return false;
    }
    
    private int guardarArchivos(String ruc) {
        int resultado=0;
        for (UploadedFile uf : upFileList) {
            try {
                FileUploadController.copyFile(FileUploadController.obtenerDestino(ruc), uf.getFileName(), uf.getInputstream());
                resultado=1;
            }catch (IOException ex) {
                System.out.println(Calendar.getInstance().getTime());
                Logger.getLogger(ProfesionalAuditorMB.class.getName()).log(Level.SEVERE, null, ex);
                return 0;
            }
        }
        return resultado;
    }
}

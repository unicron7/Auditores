/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.auditores.beans;

import ec.gob.arcom.auditoresmineros.persistencia.daos.CursoSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Curso;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Will
 */
@Named(value = "cursoMB")
@RequestScoped
public class CursoMB {
    @EJB
    private CursoSBLocal cursoSB;
    
    private List<Curso> cursos;
    
    /**
     * Creates a new instance of CursoMB
     */
    public CursoMB() {
    }
    
    @PostConstruct
    private void init() {
        this.cursos= this.cursoSB.listByEstado();
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(List<Curso> cursos) {
        this.cursos = cursos;
    }
    
}

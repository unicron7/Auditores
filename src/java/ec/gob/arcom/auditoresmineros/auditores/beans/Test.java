/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.auditores.beans;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author Will
 */
@Named(value = "test")
@SessionScoped
public class Test implements Serializable {

    /**
     * Creates a new instance of Test
     */
    public Test() {
    }
    
}

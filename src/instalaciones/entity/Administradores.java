package instalaciones.entity;
// Generated 13-jun-2016 13:18:24 by Hibernate Tools 4.3.1



/**
 * Administradores generated by hbm2java
 */
public class Administradores  implements java.io.Serializable {


     private String dniAdmin;
     private String nombre;
     private String apellidos;
     private String email;
     private String pass;

    public Administradores() {
    }

	
    public Administradores(String dniAdmin, String nombre) {
        this.dniAdmin = dniAdmin;
        this.nombre = nombre;
    }
    public Administradores(String dniAdmin, String nombre, String apellidos, String email, String pass) {
       this.dniAdmin = dniAdmin;
       this.nombre = nombre;
       this.apellidos = apellidos;
       this.email = email;
       this.pass = pass;
    }
   
    public String getDniAdmin() {
        return this.dniAdmin;
    }
    
    public void setDniAdmin(String dniAdmin) {
        this.dniAdmin = dniAdmin;
    }
    public String getNombre() {
        return this.nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellidos() {
        return this.apellidos;
    }
    
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPass() {
        return this.pass;
    }
    
    public void setPass(String pass) {
        this.pass = pass;
    }




}



package instalaciones.ui;

import instalaciones.entity.Instalaciones;
import instalaciones.entity.Reservas;
import instalaciones.entity.Usuarios;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author jcalzado
 */
public class Funciones {
    
    /**
     * Llena el JComboBox de las Instalaciones los valores obtenidos de la base de datos.
     * @param combo JComboBox a llenar.
     * @param s Sesión de la que obtendrá la consulta.
     */    
    public static void llenarComboInstalaciones(JComboBox combo, Session s) {
        String consulta = "from Instalaciones";
        Query q = s.createQuery(consulta);
        List resultList = (List) q.list();
        // Iteración para llenar el ComboBox con todas las instalaciones.
        for(Object o : resultList) {
            Instalaciones ins = (Instalaciones)o;
            combo.addItem(ins.getNombre());
        }
    }
    
    /** Llena la JTable con las horas de una determinada intalación para una fecha dada.
     * 
     * @param tabla JTable que se llenará.
     * @param s Sesión de la que se obtendrán las consultas.
     * @param fecha Fecha para la consulta.
     * @param instalacion Nombre de la instalación de la consulta.
     */
    public static void llenarTablaHoras(JTable tabla, Session s, Date fecha, String instalacion) {
        // Formateado de la fecha
        String fechaCad = new SimpleDateFormat("yyyy-MM-dd").format(fecha);
        String consulta = "from Reservas R where R.fecha='"+fechaCad+"' and R.instalaciones.nombre='"+instalacion+"' order by hora asc";
        Query q = s.createQuery(consulta);
        List resultList = (List) q.list();
        
        // Encabezados de la tabla
        Vector<String> tableHeaders = new Vector<String>();
        Vector tableData = new Vector();
        tableHeaders.add("Hora de la reserva"); 
        tableHeaders.add("Titular de la reserva");

        // Iteraciones para cargar los datos en las filas.
        for(Object o : resultList) {
            Reservas reserva = (Reservas)o;
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(reserva.getHora());
            oneRow.add(reserva.getUsuarios().getNombre());
            tableData.add(oneRow);
        }
        
        // Carga del modelo de la JTable.
        tabla.setModel(new DefaultTableModel(tableData, tableHeaders));
        
        s.getTransaction().commit();
    }
    
    /**
     * Obtiene todas las reservas y las lista en una tabla en base a unos filtros.
     * @param txtDia JTextField para el día de la reserva.
     * @param txtMes JTextField para el mes de la reserva.
     * @param txtAnio JTextField para el año de la reserva.
     * @param txtHora JTextField para la hora de la reserva.
     * @param txtMinuto JTextField para el minuto de la reserva.
     * @param txtDni JTextField para el DNI del titulas de la reserva.
     * @param comboInst JComboBox para la instalación asociada a la reserva.
     * @param tabla Tabla en la que se listarán todas las reservas.
     * @param s Sesión para realizar las operaciones con la base de datos.
     */
    public static void listarReservas(JTextField txtDia, JTextField txtMes,JTextField txtAnio,JTextField txtHora,JTextField txtMinuto,JTextField txtDni, JComboBox comboInst, JTable tabla, Session s) {
        s.beginTransaction();
        
        // Asignamos el comodín '%' por si está vacio el TextField, si no lo está
        // le asignamos el valor del TextField, controlando todo con los condicionales
        String dia = "%";       if (txtDia.getText().length()!=0) dia = txtDia.getText();
        String mes = "%";       if (txtMes.getText().length()!=0) mes = txtMes.getText();
        String anio = "%";      if (txtAnio.getText().length()!=0) anio = txtAnio.getText();
        String hora = "%";      if (txtHora.getText().length()!=0) hora = txtHora.getText();
        String minuto = "%";    if (txtMinuto.getText().length()!=0) minuto = txtMinuto.getText();
        String dni = "%";       if (txtDni.getText().length()!=0) dni = txtDni.getText();
        String inst = comboInst.getSelectedItem().toString();
        
        String consulta = "from Reservas R where R.fecha like '"+anio+"-"+mes+"-"+dia+"' and R.hora like '"+hora+":"+minuto+":00' and R.instalaciones.nombre='"+inst+"' and R.usuarios.dniUsuario like '%"+dni+"%'";
        Query q = s.createQuery(consulta);
        List resultList = (List) q.list();
        
        // Encabezados de la tabla
        Vector<String> tableHeaders = new Vector<String>();
        Vector tableData = new Vector();
        tableHeaders.add("Nº Reserva"); 
        tableHeaders.add("Instalación");
        tableHeaders.add("Usuario");
        tableHeaders.add("Fecha");
        tableHeaders.add("Hora");

        // Iteramos para cargar los datos en las filas.
        for(Object o : resultList) {
            Reservas reserva = (Reservas)o;
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(reserva.getLocalizador());
            oneRow.add(reserva.getInstalaciones().getNombre());
            oneRow.add(reserva.getUsuarios().getDniUsuario());
            oneRow.add(reserva.getFecha());
            oneRow.add(reserva.getHora());
            tableData.add(oneRow);
        }
        
        // Cargamos el modelo de la JTable.
        tabla.setModel(new DefaultTableModel(tableData, tableHeaders));
        
        s.getTransaction().commit();
    }
    
    /**
     * Llenado de la tabla con los datos de los usuarios.
     * @param txtDni JTextField con el DNI del usuario.
     * @param txtNombre JTextField con el nombre del usuario.
     * @param txtApellidos JTextField con los apellidos del usuario.
     * @param tabla Tabla en la que se cargarán los datos.
     * @param s Sesión para realizar las operaciones con la base de datos.
     */ 
    public static void llenarTablaUsuarios(JTextField txtDni, JTextField txtNombre,JTextField txtApellidos,JTable tabla, Session s) {
        // Asignamos el comodín '%' por si está vacio el TextField, si no lo está
        // le asignamos el valor del TextField, controlando todo con los condicionales
        String dni = "%";           if (txtDni.getText().length()!=0) dni = txtDni.getText();
        String nombre = "%";        if (txtNombre.getText().length()!=0) nombre = txtNombre.getText();
        String apellidos = "%";     if (txtApellidos.getText().length()!=0) apellidos = txtApellidos.getText();
        String consulta = "from Usuarios U where U.dniUsuario like '%"+dni+"%' and U.nombre like '"+nombre+"' and U.apellidos like '"+apellidos+"'";
        
        s.beginTransaction();
        Query q = s.createQuery(consulta);
        List resultList = (List) q.list();
        
        // Encabezados de la tabla
        Vector<String> tableHeaders = new Vector<String>();
        Vector tableData = new Vector();
        tableHeaders.add("DNI"); 
        tableHeaders.add("Nombre");
        tableHeaders.add("Apellidos");
        
        // Iteramos para cargar los datos en las filas.
        for(Object o : resultList) {
            Usuarios usuario = (Usuarios)o;
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(usuario.getDniUsuario());
            oneRow.add(usuario.getNombre());
            oneRow.add(usuario.getApellidos());
            tableData.add(oneRow);
        }
        
        // Cargamos el modelo de la JTable.
        tabla.setModel(new DefaultTableModel(tableData, tableHeaders));
        
        s.getTransaction().commit();
    }
        
    /**
     * 
     * @param txtDni JTextField con el DNI del usuario.
     * @param txtNombre JTextField con el nombre del usuario.
     * @param txtApellidos JTextField con los apellidos del usuario.
     * @param txtTelefono JTextField con el teéfono del usuario.
     * @param tabla Tabla en la que se cargarán los datos.
     * @param s Sesión para realizar las operaciones con la base de datos.
     */
    public static void llenarTablaUsuarios(JTextField txtDni, JTextField txtNombre,JTextField txtApellidos, JTextField txtTelefono,JTable tabla, Session s) {
        // Asignamos el comodín '%' por si está vacio el TextField, si no lo está
        // le asignamos el valor del TextField, controlando todo con los condicionales
        String dni = "%";           if (txtDni.getText().length()!=0) dni = txtDni.getText();
        String nombre = "%";        if (txtNombre.getText().length()!=0) nombre = txtNombre.getText();
        String apellidos = "%";     if (txtApellidos.getText().length()!=0) apellidos = txtApellidos.getText();
        String telefono = "%";     if (txtTelefono.getText().length()!=0) telefono = txtTelefono.getText();
        String consulta = "from Usuarios U where U.dniUsuario like '%"+dni+"%' and U.nombre like '%"+nombre+"%' and U.apellidos like '%"+apellidos+"%' and U.telefono like '%"+telefono+"%'";
        
        s.beginTransaction();
        Query q = s.createQuery(consulta);
        List resultList = (List) q.list();
        
        // Encabezados de la tabla
        Vector<String> tableHeaders = new Vector<String>();
        Vector tableData = new Vector();
        tableHeaders.add("DNI de usuario"); 
        tableHeaders.add("Nombre");
        tableHeaders.add("Apellidos");
        tableHeaders.add("Teléfono");
        tableHeaders.add("Correo electrónico");
        
        // Iteramos para cargar los datos en las filas.
        for(Object o : resultList) {
            Usuarios usuario = (Usuarios)o;
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(usuario.getDniUsuario());
            oneRow.add(usuario.getNombre());
            oneRow.add(usuario.getApellidos());
            oneRow.add(usuario.getTelefono());
            oneRow.add(usuario.getEmail());
            tableData.add(oneRow);
        }
        
        // Cargamos el modelo de la JTable.
        tabla.setModel(new DefaultTableModel(tableData, tableHeaders));
        
        s.getTransaction().commit();
        
    }
    
    /**
     * Creación de una nueva reserva y envío a la base de datos.
     * @param instalacion Nombre de la instalación.
     * @param usuario DNI del usuario titular de la reserva.
     * @param fecha Fecha de la reserva.
     * @param hora Hora de la reserva.
     * @param s Sesión para realizar las operaciones con la base de datos.
     * @throws ParseException 
     */
    public static void crearReservaBD(String instalacion, String usuario, Date fecha, String hora, Session s) throws ParseException {
        s.beginTransaction();
        // Formateamos la fecha
        String fechaCad = new SimpleDateFormat("yyyy-MM-dd").format(fecha);
        
        // Consulta para ver si ya existe una reserva en la instalación con la hora seleccionada.
        String consultaExistencia = "from Reservas R where R.instalaciones.nombre='"+instalacion+"' and R.fecha='"+fechaCad+"' and R.hora='"+ hora +"'";
        
        Query q = s.createQuery(consultaExistencia);
        List resultList = (List) q.list();
        
        // Operamos en función de si la reserva ya existe o no.
        if (!resultList.isEmpty()) {
            // Si la reserva ya existe en la tupla instalaciones/fecha/hora no se podrá crear.
            JOptionPane.showMessageDialog(null, "Ya existe una reserva en la hora seleccionada.");
            s.getTransaction().commit();
        } else {
            // Creamos el objeto Instalaciones
            Query qInst = s.createQuery("from Instalaciones I where I.nombre='"+ instalacion +"'");
            Instalaciones insIndex = (Instalaciones) qInst.uniqueResult();
            Instalaciones i = (Instalaciones)s.get(Instalaciones.class, insIndex.getIdInstalacion());
            // Creamos el objeto Usuarios
            Query qUsr = s.createQuery("from Usuarios U where U.dniUsuario='"+ usuario +"'");
            Usuarios usrIndex = (Usuarios) qUsr.uniqueResult();
            Usuarios u = (Usuarios)s.get(Usuarios.class, usrIndex.getDniUsuario());
            // Creamos la Hora
            SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
            Date h = formatoHora.parse(hora);
            
            // Creamos el objeto Reservas y lo enviamos a la BD.
            Reservas r = new Reservas(i,u,fecha,h);
            s.save(r);
            s.getTransaction().commit();
            JOptionPane.showMessageDialog(null, "Reserva añadida correctamente.");
        }
    }
    
    /**
     * Llenado de la tabla de Instalaciones.
     * @param txtNombre JTextField con el nombre de la instalación.
     * @param txtPoblacion JTextField con el la población de la instalación.
     * @param tabla Tabla a llenar con los datos de las instalaciones.
     * @param s Sesión para realizar las operaciones con la base de datos.
     */
    public static void llenarTablaInstalaciones(JTextField txtNombre, JTextField txtPoblacion, JTable tabla, Session s) {
        // Asignamos el comodín '%' por si está vacio el TextField, si no lo está
        // le asignamos el valor del TextField, controlando todo con los condicionales
        String nombre = "%";           if (txtNombre.getText().length()!=0) nombre = txtNombre.getText();
        String poblacion = "%";        if (txtPoblacion.getText().length()!=0) poblacion = txtPoblacion.getText();
        
        String consulta = "from Instalaciones I where I.nombre like '%"+nombre+"%' and I.poblacion like '%"+poblacion+"%'";
        s.beginTransaction();
        Query q = s.createQuery(consulta);
        List resultList = (List) q.list();
        
        // Encabezados de la tabla
        Vector<String> tableHeaders = new Vector<String>();
        Vector tableData = new Vector();
        tableHeaders.add("Nº Instalación"); 
        tableHeaders.add("Nombre");
        tableHeaders.add("Población");
        
        // Iteramos para cargar los datos en las filas.
        for(Object o : resultList) {
            Instalaciones instalacion = (Instalaciones)o;
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(instalacion.getIdInstalacion());
            oneRow.add(instalacion.getNombre());
            oneRow.add(instalacion.getPoblacion());
            tableData.add(oneRow);
        }
        
        // Cargamos el modelo de la JTable.
        tabla.setModel(new DefaultTableModel(tableData, tableHeaders));
        
        s.getTransaction().commit();
    }
    
    /**
     * Inserción de un nuevo usuario o actualización de uno ya existente.
     * @param dni DNI del usuario a operar.
     * @param nombre Nombre del usuario.
     * @param apellidos Apellidos del usuario
     * @param email Correo electrónico del usuario.
     * @param telefono Teléfono del usuario.
     * @param s Sesión para realizar las operaciones con la base de datos.
     */
    public static void insertarActualizarUsuario(JTextField dni, JTextField nombre, JTextField apellidos, JTextField email, JTextField telefono, Session s) {
        // Creación del objeto Usuarios y asignación de los nuevos valores.
        Usuarios u = new Usuarios();
        u.setDniUsuario(dni.getText());
        u.setNombre(nombre.getText());
        u.setApellidos(apellidos.getText());
        u.setEmail(email.getText());
        u.setTelefono(telefono.getText());
        
        // Inserción en la BD.
        s.beginTransaction();
        s.saveOrUpdate(u);
        s.getTransaction().commit();
    }
    
    /**
     * Inserción de una nueva instalación o actualización de una ya existente.
     * @param id Identificador de la instalación.
     * @param nombre Nombre de la instalación.
     * @param poblacion Población de la instalación.
     * @param s Sesión para realizar las operaciones con la base de datos.
     */
    public static void insertarActualizarInstalacion (JTextField id, JTextField nombre, JTextField poblacion, Session s) {
        
        // Creación del objeto Instalaciones
        Instalaciones i = new Instalaciones();
        
        // Comprobamos si el TextField está vacío (inserción) o no (actualización)
        if(id.getText().length() == 0) {
            //Inserción
            i.setNombre(nombre.getText());
            i.setPoblacion(poblacion.getText());
            s.beginTransaction();
            s.save(i);
            s.getTransaction().commit();
            JOptionPane.showMessageDialog(null, "Instalación añadida correctamente.");
        } else {
            i.setIdInstalacion(Integer.parseInt(id.getText()));
            i.setNombre(nombre.getText());
            i.setPoblacion(poblacion.getText());
            s.update(i);            
            JOptionPane.showMessageDialog(null, "Instalación actualizada correctamente.");
        }
        
    }
    
    /**
     * Envío de datos al JFrame para la actualización de un usuario.
     * @param tabla JTable del que se obtendrá el DNI del usuario.
     * @param s Sesión para realizar las operaciones con la base de datos.
     * @param dni JTextField en el que se cargará el DNI del usuario.
     * @param nombre JTextField en el que se cargará el nombre del usuario.
     * @param apellidos  JTextField en el que se cargarán los apellidos del usuario.
     * @param tlf JTextField en el que se cargará el teléfono del usuario.
     * @param email JTextField en el que se cargará el correo electrónico del usuario.
     * @throws NullPointerException 
     */
    public static void enviarDatosUsuario(JTable tabla, Session s, JTextField dni, JTextField nombre, JTextField apellidos, JTextField tlf, JTextField email) throws NullPointerException {
        // Obtenemos del la tabla el DNI del usuario
        String dniUser = tabla.getValueAt(tabla.getSelectedRow(), 0).toString();
        
        s.beginTransaction();
        // Recogemos el usuario de la base de datos.
        Usuarios usuario = (Usuarios) s.get(Usuarios.class, dniUser);        
        s.getTransaction().commit();
        // Vaciamos los datos de la transacción para liberarla
        s.clear();
        
        // Cargamos los datos en los campos del JFrame
        dni.setText(dniUser);
        nombre.setText(usuario.getNombre());
        apellidos.setText(usuario.getApellidos());
        tlf.setText(usuario.getTelefono());
        email.setText(usuario.getEmail());
        
    }
    
    /**
     * Envío de datos al JFrame para la actualizaición de las instalaciones.
     * @param tabla JTable del que se obtendrá el ID de la instalación a actualizar.
     * @param s Sesión para realizar las operaciones con la base de datos.
     * @param id JTextField en el que se cargará el ID de la instalación.
     * @param nombre JTextField en el que se cargará el nombre de la instalación.
     * @param poblacion JTextField en el que se cargará la población de la instalación.
     */
    public static void enviarDatosInstalacion (JTable tabla, Session s, JTextField id, JTextField nombre, JTextField poblacion) {
        // Obtenemos del la tabla el ID de la instalación.
        int idInstalacion =  Integer.parseInt(tabla.getValueAt(tabla.getSelectedRow(), 0).toString());
        
        s.beginTransaction();
        // Recogemos la instalación de la base de datos.
        Instalaciones instalacion = (Instalaciones) s.get(Instalaciones.class, idInstalacion);        
        s.getTransaction().commit();
        // Vaciamos los datos de la transacción para liberarla
        s.clear();
        
        // Cargamos los datos en los campos del JFrame
        id.setText(instalacion.getIdInstalacion().toString());
        nombre.setText(instalacion.getNombre());
        poblacion.setText(instalacion.getPoblacion());
    }
    
    /**
     * Borrado de un usuario seleccionado en la tabla.
     * @param tabla JTable del que se obtendrá el DNI del usuario.
     * @param s Sesión para realizar las operaciones con la base de datos.
     */
    public static void borrarUsuario(JTable tabla, Session s) {
        // Obtenemos el DNI del usuario seleccionado en la tabla.
        String dniUser = tabla.getValueAt(tabla.getSelectedRow(), 0).toString();
        
        s.beginTransaction();
        // Obtenemos el objeto del usuario asociado al DNI
        Usuarios usuario = (Usuarios) s.get(Usuarios.class, dniUser);
        // Procedemos al borrado
        s.delete(usuario);
        
        s.getTransaction().commit();
    }
    
    /**
     * Borrado de una reserva seleccionada en una tabla.
     * @param tabla JTable del que se obtendrá el localizador de la reserva.
     * @param s  Sesión para realizar las operaciones con la base de datos.
     */
    public static void borrarReserva(JTable tabla, Session s) {
        // Obtenemos el localizador de la reserva en base a la fila seleccionada
        int localizador =  Integer.parseInt(tabla.getValueAt(tabla.getSelectedRow(), 0).toString());
        
        s.beginTransaction();
        // Obtención del objeto Reservas en base al localizador obtenido
        Reservas reserva = (Reservas) s.get(Reservas.class, localizador);
        // Procedemos al borrado de la reserva
        s.delete(reserva);
        
        s.getTransaction().commit();
    }
    
    /**
     * Borrado de una instalación en base a una fila seleccionada en una tabla.
     * @param tabla JTable del que se obtendrá el ID de la instalación.
     * @param s Sesión para realizar las operaciones con la base de datos.
     */
    public static void borrarInstalacion(JTable tabla, Session s) {
        // Obtenemos el ID de la instalación a borrar según la fila seleccionada.
        int id =  Integer.parseInt(tabla.getValueAt(tabla.getSelectedRow(), 0).toString());
        
        s.beginTransaction();
        // Obtenemos el objeto asociado a ese ID
        Instalaciones instalacion = (Instalaciones) s.get(Instalaciones.class, id);
        // Procedemos al borrado.
        s.delete(instalacion);
        
        s.getTransaction().commit();
    }

}

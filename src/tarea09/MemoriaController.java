package tarea09;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

/**
 * Clase Controladora
 *
 * @author Ana Mesones Calvillo
 */
public class MemoriaController implements Initializable {

    // definición de variables internas para el desarrollo del juego
    private JuegoMemoria juego;         // instancia que controlará el estado del juego (tablero, parejas descubiertas, etc)
    private ArrayList<Button> cartas;   // array para almacenar referencias a las cartas @FXML definidas en la interfaz 
    private int segundos = 0;             // tiempo de juego
    private boolean primerBotonPulsado = false, segundoBotonPulsado = false; // indica si se han pulsado ya los dos botones para mostrar la pareja
    private int idBoton1, idBoton2;     // identificadores de los botones pulsados
    private boolean esPareja;           // almacenará si un par de botones pulsados descubren una pareja o no

    @FXML private AnchorPane main;      // panel principal (incluye la notación @FXML porque hace referencia a un elemento de la interfaz)
    @FXML private Label intentos;       // número de intentos de hacer parejas
    @FXML private Label tiempoJuego;    // cantidad de tiempo de juego transcurrido
    // Si declaro todos los botones en una sola línea no me los coge bien Scene Builder
    @FXML private Button carta0;
    @FXML private Button carta1;
    @FXML private Button carta2;
    @FXML private Button carta3;
    @FXML private Button carta4;
    @FXML private Button carta5;
    @FXML private Button carta6;
    @FXML private Button carta7;
    @FXML private Button carta8;
    @FXML private Button carta9;
    @FXML private Button carta10;
    @FXML private Button carta11;
    @FXML private Button carta12;
    @FXML private Button carta13;
    @FXML private Button carta14;
    @FXML private Button carta15;

    @FXML private ImageView victoria; // imagen de victoria en un imageView oculto por defecto
    @FXML private GridPane tablero; //gridPane que contiene los botones de las cartas

    // linea de tiempo para gestionar la finalización del intento al pasar 1.5 segundos
    private final Timeline finIntento = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
        finalizarIntento();
        activarBotones(); // activamos los botones de las cartas
    }));
    // linea de tiempo para gestionar la salida del juego al pasar 1.5 segundos después de pulsar ok
    private final Timeline salirJuego = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> Platform.exit()));
    // linea de tiempo para gestionar el contador de tiempo del juego
    private Timeline contadorTiempo;
    // String donde guardar el tipo de juego random que ha salido
    String juegoRandom;
    // variable contadora del numero de intentos de hacer parejas
    int numeroIntentos;
    // Reproductores de musica
    MediaPlayer player; //reproductor principal
    MediaPlayer player2; //reproductor secundario para sonidos puntuales

    /**
     * Método interno que configura todos los aspectos necesarios para
     * inicializar el juego.
     *
     * @param url No utilizaremos este parámetro (null).
     * @param resourceBundle No utilizaremos este parámetro (null).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        juego = new JuegoMemoria(); // instanciación del juego (esta instancia gestionará el estado de juego)
        juego.iniciarJuego();       // comienzo de una nueva partida
        cartas = new ArrayList<>(); // inicialización del ArrayList de referencias a cartas @FXML

        // guarda en el ArrayList "cartas" todas las referencias @FXML a las cartas para gestionarlo cómodamente
        cartas.add(carta0);
        cartas.add(carta1);
        cartas.add(carta2);
        cartas.add(carta3);
        cartas.add(carta4);
        cartas.add(carta5);
        cartas.add(carta6);
        cartas.add(carta7);
        cartas.add(carta8);
        cartas.add(carta9);
        cartas.add(carta10);
        cartas.add(carta11);
        cartas.add(carta12);
        cartas.add(carta13);
        cartas.add(carta14);
        cartas.add(carta15);

        // inicialización de todos los aspectos necesarios
        numeroIntentos = 0;
        intentos.setText("0"); //seteamos la etiqueta de intentos a 0
        tiempoJuego.setText("0"); //seteamos la etiqueta del tiempo de juego a 0
        juegoRandom = juego.getTipoPartida(); //esto nos indica el set de cartas que se va a usar en la partida
        //se establecen los dos botones pulsados a false y asi si pulsamos un boton y empezamos una nueva partida no aparece como pulsado
        primerBotonPulsado=false;
        segundoBotonPulsado=false;

        // contador de tiempo de la partida (Duration indica cada cuanto tiempo se actualizará)
        contadorTiempo = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            /// acciones a realizar (este código se ejecutará cada segundo)
            /* Se declara e inicia un String que será el valor de la variable int 
               * segundosTranscurridos y le se asigna a la Label tiempoJuego, se incrementan  
               * el valor de segundosTranscurridos según la función lambda (cada segundo)*/
            segundos++;
            String text = String.valueOf(segundos); //necesitamos el valor de segundos como un String
            tiempoJuego.setText(text); //asignamos el string con el valor de los segundos a la etiqueta tiempoJuego
        }));
        contadorTiempo.setCycleCount(Timeline.INDEFINITE);  // reproducción infinita
        contadorTiempo.play();                                // iniciar el contador en este momento

        // música de fondo para que se reproduzca cuando se inicia el juego
        String rutaMusica = System.getProperty("user.dir") + File.separator + "src" + File.separator + "tarea09" + File.separator + "assets" + File.separator + "sonidos" + File.separator + "musica.mp3";
        // Music by u_4rz47emqxz from Pixabay https://pixabay.com/es/music/video-juegos-8-bit-game-music-122259/
        try { //se usa Media, como en el ejemplo que vimos en clase
            Media musica = new Media(new File(rutaMusica).toURI().toString());
            player = new MediaPlayer(musica);
            player.setAutoPlay(true);
            player.setCycleCount(MediaPlayer.INDEFINITE);
        } catch (MediaException e) {
            System.err.println("No se encontró el archivo de sonido " + rutaMusica);
            System.exit(0);
        } //fin try-catch
    } //fin initialize

    /**
     * Acción asociada al botón <strong>Comenzar nuevo juego</strong> que
     * permite comenzar una nueva partida.
     *
     * Incluye la notación @FXML porque será accesible desde la interfaz de
     * usuario
     *
     * @param event Evento que ha provocado la llamada a este método
     */
    @FXML private void reiniciarJuego(ActionEvent event) {
        // detener el contador de tiempo 
        contadorTiempo.stop();
        segundos = 0; //reiniciamos los segundos a 0
        // detener la reproducción de la música de fondo
        player.stop();
        /* hacer visibles las 16 cartas de juego ya que es posible que no todas estén visibles 
           si se encontraron parejas en la partida anterior */
        for (int i = 0; i < juego.getTablero().size(); i++) {
            cartas.get(i).setVisible(true); //hacemos visibles los botones de las cartas
            cartas.get(i).setGraphic(null); //quitamos las imagenes asignadas a los botones
        }
        victoria.setVisible(false); //ocultamos la imagen de victoria
        tablero.setVisible(true); //hacemos visible el tablero, el gridPane con los botones de cartas
        // llamar al método initialize para terminar de configurar la nueva partida
        initialize(null, null);
    }

    /**
     * Este método deberá llamarse cuando el jugador haga clic en cualquiera de
     * las cartas del tablero.
     *
     * Incluye la notación @FXML porque será accesible desde la interfaz de
     * usuario
     *
     * @param event Evento que ha provocado la llamada a este método (carta que
     * se ha pulsado)
     */
    @FXML private void mostrarContenidoCasilla(ActionEvent event) {
        //Queremos saber cuál es la carta que ha pulsado el jugador
        String cartaId = ((Button) event.getSource()).getId(); // obtener el ID de la carta pulsada
        //sabiendo que los id de carta son del tipo carta# sacamos el número de la carta con un substring
        int id = Integer.parseInt(cartaId.substring(5, cartaId.length()));
        //Sabiendo el boton de la carta que se ha pulsado, buscamos qué imagen le correspondería según la posición del tablero
        int numeroCarta = Integer.parseInt(juego.getTablero().get(id));

        // gestionar correctamente la pulsación de las cartas (si es la primera o la segunda)
        if (!primerBotonPulsado) { //si el primer boton no se ha pulsado 
            idBoton1 = id; //se asigna la id del boton al primer boton
            primerBotonPulsado = true; //se cambia a verdadero porque ahora si ha sido pulsado el primer boton
        } else if (!segundoBotonPulsado) { //si el segundo boton no ha sido pulsado
            idBoton2 = id; //se asigna la id del boton al segundo boton
            segundoBotonPulsado = true; //se cambia a verdadero porque ahora si ha sido pulsado el segundo boton
        } // fin if-else-if

        // descubrir la imagen asociada a cada una de las cartas (y ajustar su tamaño al tamaño del botón)
        try (FileInputStream input = new FileInputStream(System.getProperty("user.dir") + File.separator + "src" + File.separator + "tarea09" + File.separator + "assets" + File.separator + "cartas" + File.separator + juegoRandom + File.separator + numeroCarta + ".png")) {
            Image carta = new Image(input);
            ImageView imageView = new ImageView(carta);
            imageView.setFitHeight(cartas.get(id).getPrefHeight() - 20); //se ajusta la altura de forma manual porque hay algun padding por ahi que no controlo
            imageView.setFitWidth(cartas.get(id).getPrefWidth() - 20); //se ajusta el ancho de forma manual porque hay algun padding por ahi que no controlo
            cartas.get(id).setGraphic(imageView); //se asigna la imagen al boton correspondiente a la id del boton pulsado
        } catch (NullPointerException e) {
            System.err.println("No existe esa imagen");
        } catch (FileNotFoundException ex) {
            System.err.println("No se ha encontrado esa imagen");
        } catch (IOException ex) {
            System.err.println("No se ha podido cargar la imagen");
        } //fin try-catch
        
        //Si no hemos pulsado el mismo boton
        if (idBoton1 != idBoton2) {
            // identificar si se ha encontrado una pareja o no
            if (primerBotonPulsado && segundoBotonPulsado) {
                esPareja = juego.compruebaJugada(idBoton1, idBoton2);
                if (!esPareja) {
                    // sonido a reproducir cuando no es pareja
                    String rutaMusica = System.getProperty("user.dir") + File.separator + "src" + File.separator + "tarea09" + File.separator + "assets" + File.separator + "sonidos" + File.separator + "noPareja.mp3";
                    // Sound Effect from Pixabay https://pixabay.com/es/sound-effects/negative-beeps-6008/
                    try { //se usa Media, como en el ejemplo que vimos en clase
                        Media musica = new Media(new File(rutaMusica).toURI().toString());
                        player2 = new MediaPlayer(musica);
                        player2.setAutoPlay(true);
                        player2.setCycleCount(1);
                    } catch (MediaException e) {
                        System.err.println("No se encontró el archivo de sonido " + rutaMusica);
                        System.exit(0);
                    } //fin try-catch
                } else {
                    // sonido a reproducir cuando es pareja
                    String rutaMusica = System.getProperty("user.dir") + File.separator + "src" + File.separator + "tarea09" + File.separator + "assets" + File.separator + "sonidos" + File.separator + "pareja.mp3";
                    // Sound Effect by floraphonic from Pixabay https://pixabay.com/es/sound-effects/cute-character-wee-2-188161/
                    try { //se usa Media, como en el ejemplo que vimos en clase
                        Media musica = new Media(new File(rutaMusica).toURI().toString());
                        player2 = new MediaPlayer(musica);
                        player2.setAutoPlay(true);
                        player2.setCycleCount(1);
                    } catch (MediaException e) {
                        System.err.println("No se encontró el archivo de sonido " + rutaMusica);
                        System.exit(0);
                    } //fin try-catch
                } //fin if-else
                //Volvemos a establecer el primer y segundo boton como no pulsados
                primerBotonPulsado = false;
                segundoBotonPulsado = false;
                numeroIntentos++; //contamos un intento de hacer pareja (dos cartas pulsadas)
                // desactivamos los botones de las cartas para que no se pueda pulsar ningun otro hasta que finalice el intento
                desactivarBotones();
                // finalizar intento (usar el timeline para que haga la llamada transcurrido el tiempo definido)
                finIntento.play();
                String text = String.valueOf(numeroIntentos); //Pasamos a String el valor del numero de intentos que llevamos
                intentos.setText(text); //se asigna el texto del numero de intentos a la etiqueta intentos
            } //fin if
        } else { //con esto evitamos que nos cuente pulsar en la misma carta
            segundoBotonPulsado = false;
        } //fin if-else
    } //fin mostrarContenidoCasilla

    //Creamos un método para activar todos los botones de las cartas
    private void activarBotones() { 
        for (Button boton : cartas) { //Para cada boton en el ArrayList cartas
            boton.setDisable(false);
        } //fin for mejorado
    } //fin activarBotones
    
    //Creamos un método para desactivar todos los botones de las cartas
    private void desactivarBotones() {
        for (Button boton : cartas) { //Para cada boton en el ArrayList cartas
            boton.setDisable(true);
            boton.setOpacity(1); //le ponemos la opacidad a 1 porque al desactivarlos se pone a 0.5 
        } //fin for mejorado
    } //fin desactivarBotones

    /**
     * Este método permite finalizar un intento realizado. Se pueden dar dos
     * situaciones:
     * <ul>
     * <li>Si se ha descubierto una pareja: en este caso se ocultarán las cartas
     * desapareciendo del tablero. Además, se debe comprobar si la pareja
     * descubierta es la última pareja del tablero y en ese caso terminar la
     * partida.</li>
     * <li>Si NO se ha descubierto una pareja: las imágenes de las cartas deben
     * volver a ocultarse (colocando su imagen a null).</li>
     * </ul>
     * Este método será interno y NO se podrá acceder desde la interfaz, por lo
     * que NO incorpora notación @FXML.
     */
    private void finalizarIntento() {
        // hacer desaparecer del tablero las cartas seleccionadas si forman una pareja
        if (esPareja) {
            cartas.get(idBoton1).setVisible(false); 
            cartas.get(idBoton2).setVisible(false);
        } //fin if
        // ocultar las imágenes de las cartas seleccionadas si NO forman una pareja
        if (!esPareja) {
            cartas.get(idBoton1).setGraphic(null);
            cartas.get(idBoton2).setGraphic(null);
        } //fin if
        // comprobar el final de partida 
        // si es final de partida mostrar el mensaje de victoria y detener el temporizador y la música
        if (juego.compruebaFin()) {
            victoria.setVisible(true); //hacemos visible el imageView con la imagen de victoria
            tablero.setVisible(false); //hacemos no visible el gridPane con los botones de cartas
            player.stop(); //Se para el reprodutor principal con la musica de fondo
            // sonido a reproducir se han encontrado todas las parejas
            String rutaMusica = System.getProperty("user.dir") + File.separator + "src" + File.separator + "tarea09" + File.separator + "assets" + File.separator + "sonidos" + File.separator + "tada.mp3";
            // Sound Effect by Sergei Chetvertnykh from Pixabay https://pixabay.com/es/sound-effects/wow-113128/
            try { //se usa Media, como en el ejemplo que vimos en clase
                Media musica = new Media(new File(rutaMusica).toURI().toString());
                player2 = new MediaPlayer(musica);
                player2.setAutoPlay(true);
                player2.setCycleCount(1); //se reproduce solo una vez
            } catch (MediaException e) {
                System.err.println("No se encontró el archivo de sonido " + rutaMusica);
                System.exit(0);
            }
            contadorTiempo.stop(); //paramos el contador del tiempo de juego
        } //fin if
    } //fin finalizarIntento

    /**
     * Este método permite salir de la aplicación. Debe mostrar una alerta de
     * confirmación que permita confirmar o rechazar la acción Al confirmar la
     * acción la aplicación se cerrará (opcionalmente, se puede incluir algún
     * efecto de despedida) Incluye la notación @FXML porque será accesible
     * desde la interfaz de usuario
     */
    @FXML private void salir() {
        player.stop(); //paramos el reproductor de musica principal con la musica de fondo
        contadorTiempo.pause(); //pausamos el contador de tiempo de juego
        // Alerta de confirmación que permita elegir si se desea salir o no del juego
        Alert exit = new Alert(Alert.AlertType.CONFIRMATION);
        exit.setTitle("It's a match!");
        exit.setContentText("¿Seguro que quieres salir?");
        Optional<ButtonType> eleccion = exit.showAndWait();

        if (eleccion.isPresent() && eleccion.get() == ButtonType.OK) { //si hemos escogido y ha sido la opcion del boton OK
            // SOLO si se confirma la acción se cerrará la ventana y el juego finalizará.
            // sonido a reproducir al salir
            String rutaMusica = System.getProperty("user.dir") + File.separator + "src" + File.separator + "tarea09" + File.separator + "assets" + File.separator + "sonidos" + File.separator + "bye_bye.mp3";
            // Sound Effect from Pixabay https://pixabay.com/es/sound-effects/ciao-ciao-88030/
            try { //se usa Media, como en el ejemplo que vimos en clase
                Media musica = new Media(new File(rutaMusica).toURI().toString());
                player = new MediaPlayer(musica);
                player.setAutoPlay(true);
                player.setCycleCount(1); //se reproduce solo una vez
            } catch (MediaException e) {
                System.err.println("No se encontró el archivo de sonido " + rutaMusica);
                System.exit(0);
            }
            player.play(); //se reproduce un sonido de despedida
            salirJuego.play(); //se inicia el timeLine de salida de juego por el que pasa 1.5 segundos desde que le damos a salir y sale
        } else {
            player.play(); //se reproduce de nuevo la musica de fondo
            contadorTiempo.play(); //vuelve a ponerse en marcha el contador del tiempo de juego
        } //fin if-else
    } //fin salir
} // fin MemoriaController
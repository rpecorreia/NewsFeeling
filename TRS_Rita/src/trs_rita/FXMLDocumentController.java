package trs_rita;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author Rita Correia
 */
public class FXMLDocumentController implements Initializable {

    ArrayList<String> tweets;

    @FXML
    Button btnnoticia;

    @FXML
    Button btnTwitter;

    @FXML
    Button btnSentimentos;

    @FXML
    TextArea textarea;

    WebEngine webengine;

    @FXML
    WebView webview;

    ArrayList<String> texto;

    ArrayList<String> totalNoticias = new ArrayList<>();

    ArrayList<String> noticias;

    double[] sentiDaNoticia;

    ConfigurationBuilder builder;

    public String pathToSL = "C:\\Users\\Rita Correia\\Desktop\\SLex.txt";

    public HashMap<String, Integer> hmap = new HashMap<String, Integer>();

    String palavra;

    ArrayList<String> SLPalavra;

    String noti = "";

    int sentimento;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            Slex();
        } catch (IOException ex) {
        }

        webview.getEngine().load("https://observador.pt/seccao/desporto/");
        tweets = new ArrayList<>();

        builder = new ConfigurationBuilder();
        builder.setDebugEnabled(true);
        builder.setOAuthConsumerKey(Keys.consumerKey);
        builder.setOAuthConsumerSecret(Keys.consumerSecret);
        builder.setOAuthAccessToken(Keys.accessToken);
        builder.setOAuthAccessTokenSecret(Keys.accessTokenSecret);

        btnnoticia.setOnAction(new EventHandler<ActionEvent>() {    //novo evento naquele botão

            @Override
            public void handle(ActionEvent e) {       //funcao a descrever o que o botao vai fazer
                try {
                    noticias = noticias();

                    for (int i = 0; i < noticias.size(); i++) {
                        String t = noticias.get(i).toString();
                        // totalNoticias.add(t);
                        noti = noti + t + "\n";
                        noti = noti + "***********************" + "\n";
                        // totalNoticias = totalNoticias + t + "\n";
                        // totalNoticias = totalNoticias + "******************************" + "\n";

                    }
                    textarea.setText(noti);

                    sentiDaNoticia = new double[noticias.size()];

                } catch (IOException e1) {
                }

            }

        });
    }

    @FXML
    public void analisaTweet(String t) throws IOException {
        String[] words = t.split("[ ,;:.!?0-9]+");
        int contador = 0;

       for (int k = 0; k < noticias.size(); k++) {
            String noticia = noticias.get(k);
            // System.out.println(noticia);          
            String[] newsWords = noticia.split("[ ,;:.!?0-9]+");

            for (int i = 0; i < newsWords.length; i++) {

                for (int j = 0; j < words.length; j++) {

                    if (words[j].length() >= 3 || newsWords[i].length() >= 3) {

                        if (newsWords[i].equals(words[j])) {
                          //  System.out.println(newsWords[i]+ " " +words[j]);
                            contador++;

                        }
                    }

                }

            }
            
            //System.out.println(contador);
            if (contador > 2) {
                //System.out.printf("==%d===> %s\n", i, t);
                analisaSentimento(t, k);
            }
            System.out.println("n " + k +" senti = "+ sentiDaNoticia[k]);
            
        }

    }

    @FXML
    public void analisaSentimento(String tweet, int idNoticia) throws IOException {
        sentimento = 0;


        String[] words = tweet.split("[ ,;:.!?0-9]+");

        System.out.println();

        //for (idNoticia = 0; idNoticia < noticias.size(); idNoticia++) {
            for (int i = 0; i < words.length; i++) {
                for (String j : hmap.keySet()) {

                    if (j.equals(words[i])) {
                        sentimento = sentimento + hmap.get(j);
                    }
                }
                

            }
            sentiDaNoticia[idNoticia] += sentimento;
            

       // }

        //System.out.println("AQUIIIIIII");
          

    }

    @FXML
    public void Sentimento() {

        Image image = null;
        
        for (int i = 0; i < sentiDaNoticia.length; i++) {
        
        if (sentiDaNoticia[i] > -0.3 && sentiDaNoticia[i] < 0.3) {
            image = new Image("/imagens/neutro.jpg");
        } else if (sentiDaNoticia[i] > -1.7 && sentiDaNoticia[i] < -0.8) {
            image = new Image("/imagens/bad.jpg");
        } else if (sentiDaNoticia[i] > 0.8 && sentiDaNoticia[i] < 1.7) {
            image = new Image("/imagens/gud.jpg");
        }

        ImageView imageView = new ImageView(image);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setGraphic(imageView);
        alert.setTitle("Sentimento Noticia "+i);
        alert.setHeaderText(null);
        alert.setContentText(null);
        alert.showAndWait();
    }

    }
    
    
    public ArrayList noticias() throws IOException {
        texto = new ArrayList<>();
        Document doc = Jsoup.connect("https://observador.pt/seccao/desporto/").get();

        Elements info = doc.select("h1.title, div.lead");

        for (Element e : info) {
            System.out.println("\n");
            if (e.hasText()) {
                texto.add(e.text());
            }
        }

        return texto;
    }

    @FXML
    public void listener() throws IOException, TwitterException {
        StatusListener listener = new StatusListener() {

            public void onStatus(Status status) {
                User u = status.getUser();
                if (!u.getLang().startsWith("pt")) {
                    return;
                }

                GeoLocation g = status.getGeoLocation();

                System.out.printf("%-30s   %-7s   [%s]: ===> %s\n",
                        status.getCreatedAt(),
                        u.getLang(),
                        u.getName(),
                        status.getText()
                );

                try {
                    analisaTweet(status.getText());
                } catch (IOException ex) {
                }
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }

            public void onException(Exception ex) {
            }

            public void onScrubGeo(long userId, long upToStatusId) {
            }

            public void onStallWarning(StallWarning warning) {
            }

        };

        TwitterStream twitterStream = new TwitterStreamFactory(builder.build()).getInstance();
        System.out.println("USER:" + twitterStream.getOAuthAccessToken().getScreenName());
        twitterStream.addListener(listener);
        twitterStream.sample();

    }

    public void Slex() throws IOException, FileNotFoundException {
        BufferedReader br = null;

        try {

            String sCurrentLine;
            SLPalavra = new ArrayList<String>();

            br = new BufferedReader(new FileReader(pathToSL));
            while ((sCurrentLine = br.readLine()) != null) {

                /*for (int i = 0; i < sCurrentLine.length(); i++) {
                    
                }*/
                hmap.put(sCurrentLine.split("\t")[0], Integer.parseInt(sCurrentLine.split("\t")[1]));

            }
            System.out.println(hmap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}

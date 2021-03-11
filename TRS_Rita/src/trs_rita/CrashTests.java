
package trs_rita;

/**
 *
 * @author Rita Correia
 */
public class CrashTests {
    public static void main(String[] args) {
        x2018Jun07at1542();
    }
    
    
    public static void x2018Jun07at1542() {
        String t="Para cada notícia  haverá  uma  lista  de  posts  associada,  com  no  máximo  100.";
        String[] words= t.split("[ ,;:.!?0-9]+");
        for (String word : words) {
            System.out.println(word);
        }
    }
}

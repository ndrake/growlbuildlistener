package net.slimeslurp.growl;

import java.io.PrintStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Map;
import java.util.HashMap;

/**
 * Java version of flip.pl/flip.js
 * 
 */ 
public class Flip {
    
    // (╯°□°）╯
    public static final String guy = "(\u256F\u00B0\u25A1\u00B0\uFF09\u256F \uFE35 ";
    
    static Map<Character,Character> flipTable = new HashMap<Character,Character>() {
    {        
        put('a','\u0250');
        put('b','q');
        put('c','\u0254'); 
        put('d','p');
        put('e','\u01DD');
        put('f','\u025F');
        put('g','\u0183');
        put('h','\u0265');
        put('i','\u0131');
        put('j','\u027E');
        put('k','\u029E');
        put('l','|');
        put('m','\u026F');
        put('n','u');
        put('o','o');
        put('p','d');
        put('q','b');
        put('r','\u0279');
        put('s','s');
        put('t','\u0287');
        put('u','n');
        put('v','\u028C');
        put('w','\u028D');
        put('x','x');
        put('y','\u028E');
        put('z','z');
        put('A','\u0250');
        put('B','q');
        put('C','\u0254');
        put('D','p');
        put('E','\u01DD');
        put('F','\u025F');
        put('G','\u0183');
        put('H','\u0265');
        put('I','\u0131');
        put('J','\u027E');
        put('K','\u029E');
        put('L','|');
        put('M','\u026F');
        put('N','u');
        put('O','o');
        put('P','d');
        put('Q','b');
        put('R','\u0279');
        put('S','s');
        put('T','\u0287');
        put('U','n');
        put('V','\u028C');
        put('W','\u028D');
        put('X','x');
        put('Y','\u028E');
        put('Z','z');
        put('.','\u02D9');
        put('[',']');
        put('\'',',');
        put(',','\'');
        put('(',')');
        put('{','{');
        put('?','\u00BF');
        put('!','\u00A1');
        put('\'',',');
        put('<','>');
        put('_','\u203E');
        put(';','\u061B');
        put(':', ':');
        put('0', '0');
        put('\n', '\n');
        put('\u203F','\u2040');
        put('\u2045','\u2046');
        put('\u2234','\u2235');
        put('\r','\n');
        put(' ',' ');
     }};
    
    
     public static String flip(String src) {
         StringBuilder sb = new StringBuilder();

         StringCharacterIterator sci = new StringCharacterIterator(src);
         for(char c = sci.last(); c != CharacterIterator.DONE; c = sci.previous()) {
             Character cf = flipTable.get(c);
             if(cf == null) {
                 sb.append("");
             } else {
                 sb.append(cf);
             }
         }
         
         return sb.toString();
     }
     
    public static void main(String[] args) {
        try {
            PrintStream out = new PrintStream(System.out, true, "UTF-8");
            out.println(guy + Flip.flip(args[0]));
        } catch(java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
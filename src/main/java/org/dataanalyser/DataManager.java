package org.dataanalyser;

import java.io.File;                //In this program, we work on Files, so this import is important
import java.io.FileReader;          //This Program does read Files
import java.io.FileWriter;          //This Program does write to our cheatsheet
import java.io.IOException;         //IOExceptions are a lot here
import java.nio.file.Files;         //More work on Files
import java.nio.file.Path;          //Later, it shows that here the files of a whole directory are worked with
import java.nio.file.Paths;         //more work with paths
import java.util.List;              //yeah, lists
import java.util.Scanner;           //Our user inputs are managed with the scanner functions
import java.util.stream.Collectors; //Theres a lot to collect
import java.util.stream.Stream;     //Theres a lot to work with streams

import org.json.simple.JSONObject;              //because here is a JsonFile in use as cheatsheet, the packages to work with them are important
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;   //No work with the parser without handling his exceptions

import net.sf.jfasta.FASTAElement;              //Fasta files are the ones, this programm want to analyse
import net.sf.jfasta.FASTAFileReader;
import net.sf.jfasta.impl.FASTAElementIterator;
import net.sf.jfasta.impl.FASTAFileReaderImpl;

public class DataManager {                      //The Name of this Class
    public static int angry = 0;                //angry and cursingSeeman are more part of an easter egg         
    public static String[] cursingSeeman = {"I'm chilling","Stop this nonsense", "you need to stop", "you make me really angry", "Stop now, or i'll kill you", "Du untruer Wendlerhörer, ich weiß genau was du machst, also hör endlich auf damit" },
    accessionNum = {"AC_","NC_","NG_","NT_","NT_","NW_","NZ_","NM_","NR_","XM_","XR_","AP_","NP_","YP_","XP_","WP_",}; //At the moment, only the Accesion numbers of the Refseq are implemented.
    public static String way = "cheatsheet.json", path = "directory";
    public static char[] basechars = {'A','C','G','T'};

    public static void main(String[] args) throws IOException, ParseException {
        
        angrychecker();
        Inputmanager();
        cheatSheetReader();
        fileAnalyser();

    }

    public static void angrychecker() throws IOException {
        do{
            File fileCheck = new File(way);
            JSONObject notice = new JSONObject();
            if (!fileCheck.exists()) {
                notice.put("comment", "Do not edit this File. This is my Cheatsheet");
                writeToCheatsheet(way, notice);
                angry++;
                continue;
            } else {
                FileReader observer = new FileReader(way);
                observer.close();
                if (angry > 1) {
                    System.out.println(cursingSeeman[angry]);
                }
                break;
            }
        } while(true);
    }

    public static void Inputmanager() throws IOException, ParseException {
        do {
            final Scanner repopath = new Scanner(System.in);
            JSONObject jamal = cheatSheetReader();
            String Analpath = (String) jamal.get(path);
            if(Analpath != null) {
                File Anal = new File(Analpath);
                if(Anal.isDirectory()){
                    System.out.println("Directory request is already satisfied, do you want to use another Directory?[y,n]");
                    String bobo = repopath.next();
                    if(bobo.equals("n")){
                        break;
                    } else if(bobo.equals("y")){
                        pathnotize(repopath);
                        break;
                    } 
                    else{
                        System.out.println("try again");
                        continue;
                    }
                }
            } else {
                    pathnotize(repopath);
                    break;
                }
            repopath.close();
        } while (true);
    }

    public static void pathnotize(Scanner repopath) throws IOException, ParseException{
        do{
            System.out.println("Please insert directory path:");
            String repo = repopath.next();
            File repodirect = new File(repo);
            if (repodirect.isDirectory()) {
                JSONObject wayson = cheatSheetReader();
                wayson.put(path,repo);
                writeToCheatsheet(way, wayson);
                break;
            } else {
                System.out.println("Input is no Directory, please insert directory");
                continue;
                }
        }while(true);
    }

    public boolean parsHelp(String parse){
        
        boolean bolsch = false;
        
        JSONParser parser = new JSONParser();
        try {
            parser.parse(new FileReader(way));
            bolsch = true;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            bolsch = false;
        }
        return bolsch;

    }

    public static JSONObject cheatSheetReader() throws IOException, ParseException {
        
        JSONParser parser = new JSONParser();

        Object eumelob = parser.parse(new FileReader(way));

        return (JSONObject) eumelob;
    }

    public static void fileAnalyser() throws IOException, ParseException {
        JSONObject eumel = cheatSheetReader();
        JSONObject fileadder = new JSONObject();
        String Analpath = (String) eumel.get(path);
        Path pfad = Paths.get(Analpath);
        
        List<Path> paths = listFiles(pfad);
        
        for(int i = 0; i < paths.size(); i++){
            featureAquise(paths.get(i).normalize().toString(), fileadder);
        }
    }
    public static List<Path> listFiles(Path path) throws IOException {

        List<Path> result;
        try (Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".fasta"))
                    .collect(Collectors.toList());
            System.out.print("Thats the result"+result);
        }
        return result;
    }
    
    public final static void featureAquise(String fasta, JSONObject fileadder) throws IOException, ParseException {

        final File file = new File(fasta);
        final FASTAFileReader freader = new FASTAFileReaderImpl(file);
        final FASTAElementIterator it = freader.getIterator();
        JSONObject features = new JSONObject();
        JSONObject wayson = cheatSheetReader();
        String relation = (String) wayson.get(path), id = "bob";

        while(it.hasNext()) {
            int[] basecount = {0, 0, 0, 0};
            float[] proportionfeature = { 0, 0, 0, 0};
            final FASTAElement el = it.next();
            String ela = el.getSequence(), head = el.getHeader();
            int seql = el.getSequenceLength();

            for(int j = 0; j< basechars.length; j++){
                for(int x = 0; x<ela.length(); x++){
                    if(ela.charAt(x)==basechars[j]){
                        basecount[j]++;
                    }
                }
            }

            for(int i = 0; i<proportionfeature.length; i++){
                proportionfeature[i]=(float) basecount[i]/(float)seql;
            }
            
            String[] array = head.split("\\|",-1);
            for(String element:array){
                for(String match:accessionNum){
                    if(element.charAt(0)=='N' && element.charAt(1)=='C' || element.charAt(0)=='P'){
                    id = element;
                    }    
                }
            }
            System.out.println("features for "+id+" in "+relation+" under calculation.\n Please wait");
            features = featuresToJSON(head, seql, basecount, proportionfeature);
            fileadder.put(id, features);
            
        }

        wayson.put(relation, fileadder);
        writeToCheatsheet(way, wayson);
        freader.close();
    }
    public static void writeToCheatsheet(String bob, JSONObject bobo) throws IOException{
        FileWriter eumel = new FileWriter(bob, false);
        eumel.write(bobo.toJSONString());
        eumel.write(System.getProperty("line.separator"));
        eumel.flush();
        eumel.close();

    }

    public static JSONObject featuresToJSON(String head,int seql, int[] basecount, float[] proportionfeature) throws IOException, ParseException {
        JSONObject feature = new JSONObject();
        String[] fb ={"proportion of ", " bases in"};
        feature.put("Headline",head);
        feature.put("Sequence length", seql);
        for(int i= 0; i<basechars.length; i++){
            feature.put(basechars[i], basecount[i]);
            feature.put(fb[0]+basechars[i]+fb[1],proportionfeature[i]);
        }
        return feature;
    }    
}
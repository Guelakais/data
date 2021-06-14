package org.dataanalyser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import net.sf.jfasta.FASTAElement;
import net.sf.jfasta.FASTAFileReader;
import net.sf.jfasta.impl.FASTAElementIterator;
import net.sf.jfasta.impl.FASTAFileReaderImpl;

public class datamanager {
    public static int angry = 0;
    public static String way = "cheatsheet.json", path = "directory";
    public static String[] cursingSeeman = {"I'm chilling","Stop this nonsense", "you need to stop", "you make me really angry", "Stop now, or i'll kill you", "Du untruer Wendlerhörer, ich weiß genau was du machst, also hör endlich auf damit" };
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
                FileWriter eumel = new FileWriter(way);
                eumel.write(notice.toJSONString());
                eumel.write(System.getProperty("line.separator"));
                eumel.flush();
                eumel.close();
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
            FileWriter eumel = new FileWriter(way, false);
            eumel.write(wayson.toJSONString());
            eumel.flush();
            eumel.close();
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

        String Analpath = (String) eumel.get(path);
        
        Path path = Paths.get(Analpath);
        
        List<Path> paths = listFiles(path);
        
        for(int i = 0; i < paths.size(); i++){
            featureAquise(paths.get(i).normalize().toString());
        }
    }
    public static List<Path> listFiles(Path path) throws IOException {

        List<Path> result;
        try (Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".fasta"))
                    .collect(Collectors.toList());
        }
        return result;
    }
    
    public final static void featureAquise(String fasta) throws IOException, ParseException {

        final File file = new File(fasta);
        final FASTAFileReader reader = new FASTAFileReaderImpl(file);
        final FASTAElementIterator it = reader.getIterator();
        JSONObject features = new JSONObject();
        JSONObject wayson = cheatSheetReader();
        String relation = (String) wayson.get(path);
        while(it.hasNext()) {
            int[] basecount = {0, 0, 0, 0};
            float[] proportionfeature = { 0, 0, 0, 0};
            final FASTAElement el = it.next();
            String ela = el.getSequence(), head = el.getHeader(), id = "bob";
            int seql = el.getSequenceLength();
            for(int x = 0; x< ela.length(); x++){
                for(int j = 0; j< basechars.length; j++){
                    if(ela.charAt(x)==basechars[j]){
                        basecount[j]++;
                    }
                }
            }
            for(int i = 0; i<proportionfeature.length; i++){
                proportionfeature[i]=(float) basecount[i]/(float)seql;
            }
            String[] array = head.split("\\|",-1);
            for(int i = 0; i<array.length;i++){
                String element = array[i];
                if(element.charAt(0)=='N' && element.charAt(1)=='C'|| element.charAt(0)=='P'){
                    id = element;
                }
            }
            System.out.println("features for "+head+" in "+relation+" under calculation.\n Please wait");
            features = featuresToJSON(id, head, seql, basecount, proportionfeature);
        }
        wayson.put(relation,features);
        eumelToJCheatSheet(way, wayson);
    }
    public static void eumelToJCheatSheet(String bob, JSONObject bobo) throws IOException{
        FileWriter eumel = new FileWriter(bob, false);
        eumel.write(bobo.toJSONString());
        eumel.write(System.getProperty("line.separator"));
        eumel.flush();
        eumel.close();

    }

    public static JSONObject featuresToJSON(String id, String head,int seql, int[] basecount, float[] proportionfeature) throws IOException, ParseException {
        JSONObject accesion = new JSONObject();
        String[] fb ={"proportion of ", " bases in"};
        accesion.put("Headline",head);
        accesion.put("Sequence length", seql);
        for(int i= 0; i<basechars.length; i++){
            accesion.put(basechars[i], basecount[i]);
            accesion.put(fb[0]+basechars[i]+fb[1],proportionfeature[i]);
        }
        return accesion;
    }
    
}
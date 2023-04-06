package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Main implements ActionListener {
    public static TextArea TextAreaLink = null;
    public static TextArea TextAreaEndLink = null;
    public static TextArea m = null;
    public static void main(String[] args) {
	// write your code here
        JFrame jFrame = new JFrame("Wiki thing");
        JPanel jPanel = new JPanel(new BorderLayout());
        jFrame.add(jPanel);
        m = new TextArea();
        m.setPreferredSize(new Dimension(500,400));
        JScrollPane scroller = new JScrollPane(m, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jPanel.add(scroller, BorderLayout.CENTER);
        JPanel jPanel2 = new JPanel(new GridLayout(1,4));
        jPanel.add(jPanel2,BorderLayout.NORTH);
        TextAreaLink = new TextArea();
        TextAreaEndLink = new TextArea();
        Button search = new Button("search recursively");
        Button bestSearch = new Button("search efficiently");
        search.setActionCommand("SR");
        bestSearch.setActionCommand("SB");
        bestSearch.addActionListener(new Main());
        JPanel buttonPanel = new JPanel(new GridLayout(2,1));
        buttonPanel.add(bestSearch);
        buttonPanel.add(search);
        search.addActionListener(new Main());
        jPanel2.add(TextAreaLink);
        jPanel2.add(TextAreaEndLink);
        jPanel2.add(buttonPanel);
        jFrame.setSize(500,500);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.requestFocus();
    }
    public static HashSet<String> f = new HashSet();
    public static ArrayList<String> wikiGame(String url, String dest, int depth){
        f.add(url);
        ArrayList<String> urls = getURls(url);
        for (String ul : urls){
            if (Objects.equals(dest, ul)){
                return new ArrayList<String>(List.of(ul));
            }
            if (depth != 0 && !f.contains(ul)) {
                ArrayList<String> m = wikiGame(ul, dest, depth - 1);
                if (m != null) {
                    m.add(url);
                    return m;
                }
            }
        }
        return null;
    }
    //The trick to this one is that it goes back and forward, halving the effective depth and massively reducing search time
    public static ArrayList<String> bestWikiGame(String URL, String dest, int MaxDepth){
        ArrayList<ArrayList<String>> urlsToGoThrough = new ArrayList<>();
        urlsToGoThrough.add(new ArrayList<String>(List.of(URL)));
        int depthCur = 0;
        int depthlimit1 = MaxDepth/2;
        int depthlimit2 = MaxDepth/2;
        if (depthlimit1 + depthlimit2 != MaxDepth){
            depthlimit1++;
        }
        while (depthCur < depthlimit1){
            depthCur++;
            ArrayList<ArrayList<String>> urlsToAdd = new ArrayList<>();
            for (ArrayList<String> a : urlsToGoThrough){
                for (String b : getURls(a.get(a.size()-1))){
                    if (!f.contains(b)) {
                        f.add(b);
                        ArrayList<String> c = new ArrayList<>(a);
                        c.add(b);
                        if (Objects.equals(b, dest)){
                            return c;
                        }
                        urlsToAdd.add(c);
                    }
               }
            }
            urlsToGoThrough = urlsToAdd;
        }
        HashMap<String, ArrayList<String>> roots = new HashMap<>();
        for (ArrayList<String> n : urlsToGoThrough){
            roots.put(n.get(n.size()-1), n);
        }
        urlsToGoThrough = new ArrayList<>(List.of(new ArrayList<>(List.of(dest))));
        depthCur = 0;
        f=new HashSet<>();
        while(depthCur < MaxDepth/2) {
            ArrayList urlsToAdd = new ArrayList<ArrayList<String>>();
            depthCur++;
            for (ArrayList<String> a : urlsToGoThrough) {
                for (String b : getBackURLs(a.get(a.size() - 1))) {
                    if (!f.contains(b)) {
                        f.add(b);
                        ArrayList<String> c = new ArrayList<>(a);
                        c.add(b);
                        if (Objects.equals(b, URL)) {
                            return c;
                        }
                        urlsToAdd.add(c);
                    }
                }
            }
            urlsToGoThrough = urlsToAdd;
        }
        for (ArrayList<String> i : urlsToGoThrough){
            if (roots.containsKey(i.get(i.size()-1))){
                ArrayList<String> c = new ArrayList<>();
                c.addAll(roots.get(i.get(i.size()-1)));
                Collections.reverse(i);
                i.remove(0);
                c.addAll(i);
                return c;
            }
        }
        return null;
    }
    public static ArrayList<String> getBackURLs(String url){
        String URL = url.replace("https://en.wikipedia.org/wiki/","https://en.wikipedia.org/wiki/Special:WhatLinksHere/");
        return getURls(URL);
    }
    public static ArrayList<String> getURls(String url){
        ArrayList<String> urls = new ArrayList<String>();
        for (String s : readURL(url)){
            if (s.contains("href=\"")) {
                int a = s.indexOf("href=\"")+6;
                int b = 0;
                for (int i = a; i < s.length(); i++) {
                    if (s.toCharArray()[i]=='\"'){
                        b=i;
                        break;
                    }
                }
                String s2 = s.substring(a,b);
                if (s2.contains("/wiki/") && !s2.contains("Main_Page") && !s2.contains("wikipedia") && !s2.contains("Help:") && !s2.contains("Special") && !s2.contains("wikimedia") && !s2.contains("Wikipedia:")){
                    urls.add("https://en.wikipedia.org"+s2);
                }
            }
        }
        return urls;
    }
    public static ArrayList<String> readURL(String urlString){
        ArrayList<String> v = new ArrayList<>();
        try {
            URL url = new URL(urlString);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while((line = br.readLine()) != null){
                v.add(line);
            }
        }
        catch(Exception ex){
            System.out.println(ex);
        }
        return v;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("SB")){
            ArrayList<String> path = null;
            int i = 1;
            while(path == null && (i != 5 && !m.getText().equals("No Path Found in reasonable time(depth 4) \n Run Again to override depth limit"))){
                f = new HashSet<>();
                path = bestWikiGame(TextAreaLink.getText(), TextAreaEndLink.getText(), i);
                i++;
            }
            if (path == null){
                m.setText("No Path Found in reasonable time(depth 4) \n Run Again to override depth limit");
            }
            else{
                m.setText(Arrays.toString(path.toArray()));
            }
        }
        else{
            ArrayList<String> path = null;
            int i = 1;
            while(path == null && (i != 3 && !m.getText().equals("No Path Found in reasonable time(depth 4) \n Run Again to override depth limit"))){
                f = new HashSet<>();
                path = wikiGame(TextAreaLink.getText(), TextAreaEndLink.getText(), i);
                i++;
            }
            if (path == null){
                m.setText("No Path Found in reasonable time(depth 2) \n Run Again to override depth limit");
            }
            else{
                m.setText(Arrays.toString(path.toArray()));
            }
        }
    }
}

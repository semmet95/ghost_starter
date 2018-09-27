/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.util.Log;

import java.util.HashMap;
import java.util.Random;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
        String tempWord="";
        TrieNode tempNode1=this, tempNode2;
        for(int i=0;i<s.length();i++) {
            tempWord+=s.charAt(i);
            tempNode2=tempNode1.children.get(tempWord);
            if(tempNode2==null) {
                tempNode2=new TrieNode();
                tempNode1.children.put(tempWord, tempNode2);
            }
            tempNode1=tempNode2;
        }
        tempNode1.isWord=true;
    }

    public boolean isWord(String s) {
        if (s.length() < 4)
            return false;
        TrieNode tempNode = searchNode(s);
        if (tempNode == null)
            return false;
        return tempNode.isWord;
    }

    public String getAnyWordStartingWith(String s) {
        if(s.length()==0)
            return "abcdefghijklmnopqrstuvwxyz".charAt(new Random().nextInt(26))+"";
        TrieNode tempNode=searchNode(s);
        if(tempNode==null)
            return null;
        return tempNode.children.keySet().toArray()[new Random().nextInt(tempNode.children.size())].toString();

    }

    public String getGoodWordStartingWith(String s) {
        if(s.length()==0)
            return "abcdefghijklmnopqrstuvwxyz".charAt(new Random().nextInt(26))+"";
        TrieNode tempNode=searchNode(s);
        if(tempNode==null)
            return null;
        for(String x: tempNode.children.keySet())
            if(!tempNode.children.get(x).isWord)
                return x;
        if("bcdfghjklmnpqrstvwxyz".contains(s.charAt(s.length()-1)+""))
            return "aeiou".charAt(new Random().nextInt(5))+"";
        return "bcdfghjklmnpqrstvwxyz".charAt(new Random().nextInt(21))+"";
    }

    TrieNode searchNode(String s) {
        TrieNode tempNode1=this, tempNode2;
        String tempWord="";
        for(int i=0;i<s.length();i++) {
            tempWord+=s.charAt(i);
            Log.e("temp node :", "searching for "+tempWord);
            tempNode2=tempNode1.children.get(tempWord);
            if(tempNode2==null)
                return null;
            tempNode1=tempNode2;
        }
        return tempNode1;
    }

   String getWordFrom(TrieNode x) {
        TrieNode tempNode=x.children.get(x.children.keySet().toArray()[new Random().nextInt(x.children.size())]);
        String tempWord=null;
        while(!tempNode.isWord) {
            tempWord=tempNode.children.keySet().toArray()[new Random().nextInt(tempNode.children.size())].toString();
            tempNode = tempNode.children.get(tempWord);
        }
        return tempWord;
    }
}

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    static ArrayList<String> validWords;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
                words.add(line.trim());
        }

        validWords=new ArrayList<>();
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if(prefix.length()==0)
            return words.get(new Random().nextInt(words.size()));
        else {
            int left=0, right=words.size()-1;
            while(left<=right) {
                int mid=(left+right)/2;
                String temp=words.get(mid);
                int compared=prefix.compareTo(temp);
                if(temp.startsWith(prefix)&&compared<0)
                    return temp;
                else if(compared<0)
                    right=mid-1;
                else if(compared>0)
                    left=mid+1;
            }
            return null;
        }
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        String selected = null;
        if(prefix.length()==0) {
            while(true) {
                String temp=words.get(new Random().nextInt(words.size()));
                if(temp.length()%2==0)
                    return temp;
            }
        }
        if(validWords.size()==0)
            initializevalidWords(prefix.length()%2!=0, prefix);

        int left=0, right=validWords.size()-1;
        //Log.e("validowrds :", "finding the selected");

        while(left<=right) {
            int mid=(left+right)/2;
            String temp=validWords.get(mid);
            int compared=prefix.compareTo(temp);
            //Log.e("validowrds :", "mid = "+mid+" prefix = "+prefix+" temp = "+temp+" compared = "+compared);
            if(temp.startsWith(prefix)&&compared<0) {
                if(isWord(temp.substring(0, prefix.length()+1))) {
                    validWords.remove(mid);
                    left=0;
                    right=validWords.size()-1;
                    continue;
                }
                selected = temp;
                break;
            }
            else if(compared<0)
                right=mid-1;
            else if(compared>0)
                left=mid+1;
        }
        //Log.e("validowrds :", "found the selected");
        if(selected==null) {
            Log.e("validowrds :", "can't win :(");
            return getAnyWordStartingWith(prefix);
        }
        return selected;
    }

    private void initializevalidWords(boolean odd, String prefix) {
        int left=0, right=words.size()-1, index=-1;
        Log.e("validowrds :", "finding prefix");
        while(left<=right) {
            int mid=(left+right)/2;
            String temp=words.get(mid);
            int compared=prefix.compareTo(temp);
            if(temp.startsWith(prefix)&&compared<0) {
                index = mid;
                break;
            }
            else if(compared<0)
                right=mid-1;
            else if(compared>0)
                left=mid+1;
        }
        Log.e("validowrds :", "prefix found at index = "+index);
        for(int i=index;i>=0;i--) {
            String temp=words.get(i);
            if(temp.startsWith(prefix)&&temp.length()>prefix.length()) {
                if((odd&&temp.length()%2!=0)||(!odd&&temp.length()%2==0))
                    validWords.add(0, temp);
            } else
                break;
        }
        for(int i=index+1;i<words.size();i++) {
            String temp=words.get(i);
            if(temp.startsWith(prefix)&&temp.length()>prefix.length()) {
                if((odd&&temp.length()%2!=0)||(!odd&&temp.length()%2==0))
                    validWords.add(temp);
            } else
                break;
        }
    }

    @Override
    public String getFullWordFrom(String prefix) {
        return null;
    }
}

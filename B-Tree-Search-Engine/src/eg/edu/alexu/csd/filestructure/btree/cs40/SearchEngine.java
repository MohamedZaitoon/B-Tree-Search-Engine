package eg.edu.alexu.csd.filestructure.btree.cs40;

import java.util.*;
import java.util.stream.Collectors;

import eg.edu.alexu.csd.filestructure.btree.IBTree;
import eg.edu.alexu.csd.filestructure.btree.ISearchEngine;
import eg.edu.alexu.csd.filestructure.btree.ISearchResult;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.ParserConfigurationException;

public class SearchEngine implements ISearchEngine {
    private IBTree bTree;
    private Parser parser;

    public SearchEngine(int t) {
        bTree = new BTree(t);
        try {
            parser = new Parser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void indexWebPage(String filePath) {
        NodeList documents = parser.getDocuments(filePath);
        indexOrDelete(documents, true);
    }

    @Override
    public void indexDirectory(String directoryPath) {
        // TODO Auto-generated method stub
        List<String> paths = Utilities.getFilePaths(directoryPath);
        if (paths == null) throw new RuntimeErrorException(null);
        for (String path : paths) indexWebPage(path);
    }

    @Override
    public void deleteWebPage(String filePath) {
        NodeList documents = parser.getDocuments(filePath);
        indexOrDelete(documents, false);
    }

    @Override
    public List<ISearchResult> searchByWordWithRanking(String word) {
        if (word == null) throw new RuntimeErrorException(null);
        List<ISearchResult> list;
        try {
            list = (List<ISearchResult>) bTree.search(word.toLowerCase());
        } catch (Exception e) {
            return new LinkedList<>();
        }
        return list;
    }

    @Override
    public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
        // TODO Auto-generated method stub
        if (sentence == null) throw new RuntimeErrorException(null);
        List<String> words = parser.getWords(sentence);
        if (words.size() == 0) return new LinkedList<>();
        List<List<ISearchResult>> results = new ArrayList<>(words.size());
        for (String word : words) results.add(searchByWordWithRanking(word));
        for (int i = 1; i < words.size(); i++) {
            if (results.get(i) == null) return new LinkedList<>();
            results.set(0, results.get(0).stream().distinct().filter(results.get(i)::contains).collect(Collectors.toList()));
        }
        return results.get(0);
    }

    private void indexOrDelete(NodeList documents, boolean index) {
        for (int i = 0; i < documents.getLength(); i++) {
            List<String> words = parser.getWords(documents.item(i).getTextContent());
            HashMap<String, Integer> map = new HashMap<>();
            for (String s : words) map.put(s, map.getOrDefault(s, 0) + 1);
            String id = ((Element) documents.item(i)).getAttribute("id");

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                List<SearchResult> node = bTree.getRoot() == null ? null : (List<SearchResult>) bTree.search(entry.getKey());
                if (node == null) {
                    if (index) {
                        List<SearchResult> list = new LinkedList<>();
                        list.add(new SearchResult(id, entry.getValue()));
                        bTree.insert(entry.getKey(), list);
                    } else return;
                } else {
                    if (index) node.add(new SearchResult(id, entry.getValue()));
                    else {
                        // delete search result corresponding to this document
                        for (SearchResult result : node) {
                            if (result.getId().equals(id)) {
                                node.remove(result);
                                break;
                            }
                        }
                        if (node.isEmpty()) bTree.delete(entry.getKey());
                    }
                }
            }
        }
    }

    public IBTree getbTree() {
        return bTree;
    }

}

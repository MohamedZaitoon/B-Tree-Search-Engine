package eg.edu.alexu.csd.filestructure.btree.cs40;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parser {
    private DocumentBuilder builder;

    Parser() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }

    NodeList getDocuments(String path) {
        if (path == null || path.isEmpty()) throw new RuntimeErrorException(null);
        Document file;
        try {
            file = builder.parse(path);
        } catch (SAXException | IOException e) {
            throw new RuntimeErrorException(null);
        }
        assert file != null;
        return file.getElementsByTagName("doc");
    }

    List<String> getWords(String paragraph) {
        Pattern pattern = Pattern.compile("[\\S]+");
        Matcher matcher = pattern.matcher(paragraph);
        List<String> words = new ArrayList<>();
        while (matcher.find()) words.add(matcher.group().toLowerCase());
        return words;
    }
}

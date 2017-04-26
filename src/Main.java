import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main
{
    static Pattern emlPattern = Pattern.compile(".*?\\.eml");

    public static void main(String [] args)
    {
        final String reviewTemplate = readFile("CardTemplate.html", Charset.defaultCharset());
        final String resultTemplate = readFile("ReviewsTemplate.html", Charset.defaultCharset());
        if(args.length > 0)
        {
            System.out.println("Searching for your reviews");
            try
            {
                File dir = new File(args[0]);
                File[] files = dir.listFiles();
                if (files != null)
                {
                    String cards = "";
                    int count = 0;
                    for (File file : files)
                    {
                        if(emlPattern.matcher(file.getName()).matches())
                        {
                            String link = parseTrustpilotLink(file);
                            boolean review = linkIsReview(link);
                            if(review)
                            {
                                String customer = parseCustomerName(file);
                                String newCard = reviewTemplate.replace("$CUSTOMER", customer);
                                newCard = newCard.replace("$LINK", link);
                                cards += newCard;
                                count++;
                            }
                        }
                    }
                    String htmlResult = resultTemplate.replace("$BODY", cards);
                    htmlResult = htmlResult.replace("$COUNT", Integer.toString(count));
                    PrintWriter out = new PrintWriter("Reviews.html");
                    out.print(htmlResult);
                    out.close();

                }
            }
            catch (Exception ex)
            {
                System.out.println("Something went wrong, Please check if your path is valid");
            }
        }
    }

    static String parseCustomerName(File eml)
    {
        Pattern namePattern = Pattern.compile("Beste ((?:mevrouw|heer) .*?),");
        try(Stream<String> lines = Files.lines(eml.toPath()))
        {
            List<String> klantNamen = new ArrayList<String>();
            lines
                    .filter(s -> namePattern.matcher(s).matches())
                    .forEach(s -> klantNamen.add(s));
            if(klantNamen.size() > 0)
            {
                Matcher m = namePattern.matcher(klantNamen.get(0));
                if(m.find())
                {
                    return m.group(1);
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
        return "";
    }



    static String parseTrustpilotLink(File eml)
    {
        Pattern linkPattern = Pattern.compile("<http:\\/\\/www.trustpilot.nl\\/(.*?)>");
        try(Stream<String> lines = Files.lines(eml.toPath()))
        {
            List<String> links = new ArrayList<String>();
            lines
                    .filter(s -> linkPattern.matcher(s).matches())
                    .forEach(s -> links.add(s));
            if(links.size() > 0)
            {
                Matcher m = linkPattern.matcher(links.get(0));
                if(m.find())
                {
                    return "https://nl.trustpilot.com/" + m.group(1);
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("Something went wrong");
        }

        return new String();
    }

    static boolean linkIsReview(String link)
    {
        try
        {
            HttpURLConnection con = (HttpURLConnection) new URL(link).openConnection();
            HttpsURLConnection sslCon = (HttpsURLConnection) con;
            sslCon.setInstanceFollowRedirects(false);
            sslCon.connect();
            if(con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP)
            {
                String newUrl = con.getHeaderField("Location");
                newUrl = "https://nl.trustpilot.com" + newUrl;
                con.disconnect();
                return true;
            }

        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
        return false;
    }

    static String readFile(String path, Charset encoding)
    {
        try
        {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        }
        catch (Exception ex)
        {
            return "";
        }
    }
}

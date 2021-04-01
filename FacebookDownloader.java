import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class LinksExtractor {
    public static void main(String[] args) throws Exception {
        File bodyHtml = new File("bodyHtml.txt");
        File linksFile = new File("Failed Links.txt");
        File fullLinksFile = new File("Full Links File.txt");
        File FinalinksFile = new File("Succeeded Links.txt");
        Scanner input = new Scanner(bodyHtml, "UTF-8");

        String currentVidId;
        String currentPageId;

        ArrayList<String> vidId = new ArrayList<>();
        ArrayList<String> pageId = new ArrayList<>();
        ArrayList<String> fullLinks = new ArrayList<>();

        double count = 0;
        int succeeded = 0;
        int failed = 0;

        while (input.hasNext()) {
            String currentNext = input.next();

            if (currentNext.contains("/videos/")) {
                String[] splitString = currentNext.split("/");
                currentPageId = splitString[1];
                currentVidId = splitString[3];
                currentVidId = currentVidId.substring(0, currentVidId.length() - 1);

                if (!vidId.contains(currentVidId)) {
                    vidId.add(currentVidId);
                    pageId.add(currentPageId);
                    String fullLink = String.format("https://www.facebook.com/%s/videos/%s/", currentPageId,
                            currentVidId);
                    fullLinks.add(fullLink);
                    count++;
                    System.out.printf("\rwe found %f urls", count);
                }
            }

        }
        count = 0;
        System.out.println();

        Collections.sort(fullLinks);
        ArrayList<String> finalLinks = new ArrayList<>();
        try (PrintWriter outLinksFile = new PrintWriter(linksFile);
                PrintWriter outFinalLinksFile = new PrintWriter(FinalinksFile);
                PrintWriter outFukkLinksFile = new PrintWriter(fullLinksFile);) {

            for (String fullLink : fullLinks) {
                outFukkLinksFile.print(fullLink);
                String finalLink = getVideoURL(fullLink);
                if (finalLink.equals("")) {
                    failed++;
                    outLinksFile.println(fullLink);
                } else {
                    succeeded++;
                    outFinalLinksFile.println(finalLink);
                }
                count++;
                System.out.printf("\rCompleted: %2.2f%% || succeeded videos: %2d || failed videos:%2d",
                        (count / fullLinks.size() * 100.0), succeeded, failed);

            }
        }
        System.out.printf("\nCongratulations!!\nsucceeded videos: %d || failed videos:%d ", succeeded, failed);

        // try (PrintWriter out = new PrintWriter(linksFile);) {

        // for (int i = 0; i < fullLinks.size(); i++) {
        // out.write(fullLinks.get(i) + "\n");
        // }

        // out.write(fullLinks.size() + " \n");

        // }

    }

    public static String getVideoURL(String u) throws Exception {
        URL url = new URL(u);
        Scanner input = new Scanner(url.openStream());
        while (input.hasNextLine()) {
            String line = input.nextLine();
            if (line.contains("hd_src:\""))
                return line.split("hd_src:\"")[1].split("\"")[0];
            else if (line.contains("sd_src:\""))
                return line.split("sd_src:\"")[1].split("\"")[0];
        }
        return "";
    }
}
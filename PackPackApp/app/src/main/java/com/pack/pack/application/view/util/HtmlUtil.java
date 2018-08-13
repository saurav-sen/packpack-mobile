package com.pack.pack.application.view.util;

/**
 * Created by Saurav on 05-08-2018.
 */
public final class HtmlUtil {

    private static final String HTML_TEMPLATE_WITH_LOGO = "<html>\n" +
            "\t<head>\n" +
            "\t\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\">\n" +
            "\t\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\t\t\n" +
            "\t\t<link rel=\"stylesheet\" href=\"w3.css\">\n" +
            "\t\t<style type=\"text/css\">\n" +
            "\t\t\timg {\n" +
            "\t\t      width: auto;\n" +
            "\t\t      height : auto;\n" +
            "\t\t      max-height: 100%;\n" +
            "\t\t      max-width: 100%;\n" +
            "\t\t    }\n" +
            "\t\t\t.no-decoration-hyperlink {\n" +
            "\t\t\t  text-decoration: none;\n" +
            "\t\t\t}\n" +
            "\t\t\t\n" +
            "\t\t\t.img-logo {\n" +
            "\t\t\t   display: block;\n" +
            "\t\t\t   margin-left: auto;\n" +
            "\t\t\t   margin-right: auto;\n" +
            "\t\t\t   max-width: 200px;\n" +
            "\t\t\t   max-height: 200px;\n" +
            "\t\t\t   height: auto;\n" +
            "\t\t\t   width: auto;\n" +
            "\t\t\t}\n" +
            "\t\t\t.header-border{\n" +
            "\t\t\t\tmargin: 3% 0;\n" +
            "\t\t\t}\n" +
            "\t\t\t.heading{\n" +
            "\t\t\t    font-size: 18px;\n" +
            "\t\t\t}\n" +
            "\t\t\t.footer-squill{\n" +
            "\t\t\t\tfloat:left;\n" +
            "\t\t\t\tmargin: 2% 0;\n" +
            "\t\t\t}\n" +
            "\t\t\t.footer-img{\n" +
            "\t\t\t\tfloat:right;\n" +
            "\t\t\t\tmargin: 1% 0;\n" +
            "\t\t\t}\t\t\t\n" +
            "\t\t\tp.fullText {\n" +
            "\t\t\t\ttext-indent: 10px;\n" +
            "\t\t\t\ttext-align: justify;\n" +
            "\t\t\t\tletter-spacing: 2px;\n" +
            "\t\t\t\tfont-family: Arial, Helvetica, sans-serif;\n" +
            "\t\t\t\tcolor: #808080;\n" +
            "\t\t\t}\n" +
            "            a.srcLink {\n" +
            "\t\t\t\ttext-decoration: none;\n" +
            "\t\t\t\tcolor: #008CBA;\n" +
            "\t\t\t}\t\n" +
            "\t\t\th4.title {\n" +
            "\t\t\t\ttext-align: center;\n" +
            "\t\t\t\ttext-transform: capitalize;\n" +
            "\t\t\t}\n" +
            "\t\t</style>\n" +
            "\t</head>\t\n" +
            "\t<body>\n" +
            "\t    <div class=\"w3-container\">\t    \n" +
            "\t\t\t<div class=\"w3-container w3-border-top w3-border-left w3-border-bottom w3-border-right\">\n" +
            "\t\t\t\t<br />\n" +
            "\t\t\t\t<br />\n" +
            "\t\t\t\t<img src=\"LOGOIMAGE\" class=\"img-logo w3-border-bottom\">\t\t\t\t\n" +
            "\t\t\t\t<header class=\"w3-container heading\">\t\t\t  \t\n" +
            "\t\t\t\t  <h4 class=\"title\">NEWSTITLE</h4>\n" +
            "\t\t\t\t</header>\t\t\t\t\n" +
            "\t\t\t\t<div class=\"w3-container\">\n" +
            "\t\t\t\t  <p class=\"fullText\">NEWSFULLTEXT <a class=\"srcLink\" target=\"_blank\" href=\"SOURCELINK\">Read From Source</a></p>\n" +
            "\t\t\t\t</div>\t\t\t\t\n" +
            "\t\t\t\t<br />\n" +
            "\t\t\t  </div>\n" +
            "\t\t\t</div>\n" +
            "\t\t\t<br />\n" +
            "\t\t</div>\t\n" +
            "\t</body>\n" +
            "</html>";

    private static final String HTML_TEMPLATE_WITHOUT_LOGO = "<html>\n" +
            "\t<head>\n" +
            "\t\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\">\n" +
            "\t\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\t\t\n" +
            "\t\t<link rel=\"stylesheet\" href=\"w3.css\">\n" +
            "\t\t<style type=\"text/css\">\n" +
            "\t\t\timg {\n" +
            "\t\t      width: auto;\n" +
            "\t\t      height : auto;\n" +
            "\t\t      max-height: 100%;\n" +
            "\t\t      max-width: 100%;\n" +
            "\t\t    }\n" +
            "\t\t\t.no-decoration-hyperlink {\n" +
            "\t\t\t  text-decoration: none;\n" +
            "\t\t\t}\n" +
            "\t\t\t\n" +
            "\t\t\t.img-logo {\n" +
            "\t\t\t   display: block;\n" +
            "\t\t\t   margin-left: auto;\n" +
            "\t\t\t   margin-right: auto;\n" +
            "\t\t\t   max-width: 200px;\n" +
            "\t\t\t   max-height: 200px;\n" +
            "\t\t\t   height: auto;\n" +
            "\t\t\t   width: auto;\n" +
            "\t\t\t}\n" +
            "\t\t\t.header-border{\n" +
            "\t\t\t\tmargin: 3% 0;\n" +
            "\t\t\t}\n" +
            "\t\t\t.heading{\n" +
            "\t\t\t    font-size: 18px;\n" +
            "\t\t\t}\n" +
            "\t\t\t.footer-squill{\n" +
            "\t\t\t\tfloat:left;\n" +
            "\t\t\t\tmargin: 2% 0;\n" +
            "\t\t\t}\n" +
            "\t\t\t.footer-img{\n" +
            "\t\t\t\tfloat:right;\n" +
            "\t\t\t\tmargin: 1% 0;\n" +
            "\t\t\t}\t\t\t\n" +
            "\t\t\tp.fullText {\n" +
            "\t\t\t\ttext-indent: 10px;\n" +
            "\t\t\t\ttext-align: justify;\n" +
            "\t\t\t\tletter-spacing: 2px;\n" +
            "\t\t\t\tfont-family: Arial, Helvetica, sans-serif;\n" +
            "\t\t\t\tcolor: #808080;\n" +
            "\t\t\t}\n" +
            "            a.srcLink {\n" +
            "\t\t\t\ttext-decoration: none;\n" +
            "\t\t\t\tcolor: #008CBA;\n" +
            "\t\t\t}\t\n" +
            "\t\t\th4.title {\n" +
            "\t\t\t\ttext-align: center;\n" +
            "\t\t\t\ttext-transform: capitalize;\n" +
            "\t\t\t}\n" +
            "\t\t</style>\n" +
            "\t</head>\t\n" +
            "\t<body>\n" +
            "\t    <div class=\"w3-container\">\t    \n" +
            "\t\t\t<div class=\"w3-container w3-border-top w3-border-left w3-border-bottom w3-border-right\">\n" +
            "\t\t\t\t<br />\n" +
            "\t\t\t\t<br />\t\t\n" +
            "\t\t\t\t<header class=\"w3-container heading\">\t\t\t  \t\n" +
            "\t\t\t\t  <h4 class=\"title\">NEWSTITLE</h4>\n" +
            "\t\t\t\t</header>\t\t\t\t\n" +
            "\t\t\t\t<div class=\"w3-container\">\n" +
            "\t\t\t\t  <p class=\"fullText\">NEWSFULLTEXT <a class=\"srcLink\" target=\"_blank\" href=\"SOURCELINK\">Read From Source</a></p>\n" +
            "\t\t\t\t</div>\t\t\t\t\n" +
            "\t\t\t\t<br />\n" +
            "\t\t\t  </div>\n" +
            "\t\t\t</div>\n" +
            "\t\t\t<br />\n" +
            "\t\t</div>\t\n" +
            "\t</body>\n" +
            "</html>";

    private HtmlUtil() {
    }

    public static String generateOfflineHtml(String newsTitle, String newsFullText, String sourceLink, String logoImage) {
        if(logoImage != null) {
                return HTML_TEMPLATE_WITH_LOGO.replaceAll("NEWSTITLE", newsTitle.replaceAll(" +", " ")
                        .replaceAll("\\t+", " ")).replaceAll("NEWSFULLTEXT", newsFullText.replaceAll(" +", " ")
                        .replaceAll("\\t+", " ")).replaceAll("SOURCELINK", sourceLink)
                        .replaceAll("LOGOIMAGE", logoImage);
        }
        return HTML_TEMPLATE_WITHOUT_LOGO.replaceAll("NEWSTITLE", newsTitle.replaceAll(" +", " ")
                .replaceAll("\\t+", " ")).replaceAll("NEWSFULLTEXT", newsFullText.replaceAll(" +", " ")
                .replaceAll("\\t+", " ")).replaceAll("SOURCELINK", sourceLink);
    }
}

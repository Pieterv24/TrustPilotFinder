### Usage: 
Exporteer alle mails met als subject "Heeft ... U goed geholpen" als eml
Ik heb hiervoor IMAPSize gebruikt.

Download de jar [Hier]("https://github.com/Pverweij/TrustPilotFinder/releases/download/1.0/TrustpilotChecker.zip")

Vervolgens plaats run je vanaf de commandline:
```java -jar TrustpilotChecker.jar "/path/to/eml/directory"```
hierna na was tijd verschijnt er een html file genaamd reviews.html in je de map waarin TrustpilotChecker staat.
	
	
### Customization:
Voel je vrij de Template files zover aan te passen als je wil.
Dit moetwerken zolang je de $UPPPERCASSE text laat staan.
	
	
### Troubleshooting
1. Help mijn Review.html is leeg
	Oplossing: Kijk of zowel CardTemplate.html en ReviewsTemplate.html zich in dezelfde map als TrustpilotChecker.jar bevinden.
2. Help hij werkt niet
	Oplossing: Ik heb geen idee. de code staat op github, kijk of je t kan fixen.
3. Help ik probeer te fixem maar tijdens debuggen loopt hij vast
	Oplossing: op sommige computers houd HTTPSUrlConnection niet van debuggen. werkt wel bij normaal runnen (Meestal)

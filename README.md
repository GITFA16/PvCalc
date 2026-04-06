Projektübersicht – PV-Leistungsabschätzung

Im Rahmen der Semesterarbeit des 1. Semesters des Nachdiplomstudiums Software Engineering an der ABB-Technikerschule wurde eine Desktop-Anwendung zur Abschätzung der momentanen Leistung von Photovoltaikanlagen entwickelt. Die Anwendung ermittelt anhand einer Schweizer Postleitzahl (PLZ) den geografischen Standort und kombiniert aktuelle Wetterdaten externer Wetterdienste mit vereinfachten physikalischen Modellen, um die elektrische Leistung einer fest installierten PV-Anlage zu berechnen. Über eine grafische Benutzeroberfläche (GUI) können neben der PLZ weitere Parameter wie Anlagenfläche, Dachneigung und PV-Modul ausgewählt werden. Die benötigten Wetter- und Strahlungsdaten (z. B. Temperatur, Bewölkung, Sonneneinstrahlung) werden automatisch über Web-APIs abgerufen und die berechneten Resultate übersichtlich dargestellt. Das Projekt umfasst unter anderem die Anbindung externer Web-APIs, die Verarbeitung von Geo- und Wetterdaten sowie die strukturierte Umsetzung der Berechnungslogik in objektorientiertem Kotlin-Code. Die Anwendung bildet eine solide Grundlage für spätere Erweiterungen, etwa detailliertere Wettermodelle oder zusätzliche PV-Modulparameter.

Entwicklungsumgebung

Dieses Projekt wurde mit folgendem Editor erstellt: IntelliJ IDEA 2025.2.4 (Community Edition) Build #IC-252.27397.103 Built on October 23, 2025 Runtime version: 21.0.8+1-b1038.73 amd64 (JCEF 122.1.9) VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.

Java Runtime

Die verwendete Java Runtime befindet sich im folgenden Verzeichnis: XY

Die Anwendung wurde erfolgreich getestet auf folgenden Plattformen:

Windows 10

Windows 11

(weitere Plattformen optional ergänzbar)

Hinweis zur Nutzung und Weitergabe

⚠️ Privates GitHub-Projekt

Dieses Repository ist ein privates GitHub-Projekt. Der Zugriff sowie die Nutzung des Codes sind ausschliesslich den für die Prüfung zuständigen Personen gestattet, welche eine entsprechende Einladung über GitHub erhalten haben.

Die Weiterverwendung des Projekts ist nur zu internen Zwecken im Rahmen der Prüfung erlaubt. Eine Weitergabe, Veröffentlichung oder Nutzung ausserhalb dieses Rahmens ist ohne ausdrückliche Zustimmung des Autors nicht gestattet.

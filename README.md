# Mobilny system przetwarzania obrazów
Praca inżynierska studenta Antoniego Charchuły, tworzona pod przewodnictwem mgr inż. Krzysztofa Grackiego.

Głównym celem pracy było stworzenie aplikacji mobilnej na system Android, która będzie obsługiwała funkcjonalonści zawarte w bibliotece OpenCV, która służy do przetwarzania obrazu.
Dodatkowo aplikacja ma pozwalać na porównywanie wydajności obsługi algorytmów uruchamianych na smartfonie oraz na serwerze zewnętrznym, który komunikuje się z aplikacją.

## Sposób uruchomienia

Cała instrukcja skierowana jest dla uzytkowników systemu Ubuntu w wersji co najmniej 18.04 oraz przedstawiane są czynności konfiguracji dla IDE o nazwie Android Studio 3.5.3 oraz Intellij IDEA 2019.3.1. Dodatkowo, wymagane jest posiadanie zainstalowanej co najmniej Javy 8. Pliki biblioteki OpenCV nalezy pobrać ze strony „sourceforge.net/projects/opencvlibrary/files/4.1.1/” – plik „OpenCV 4.1.1.zip” oraz „opencv-4.1.1-android-sdk.zip”.

Kolejne kroki konfiguracji środowiska dla programu serwera:
- Zainstaluj bibliotekę ant używając komendy: "sudo apt-get install ant"
- Rozpakuj plik "OpenCV 4.1.1.zip''
- W rozpakowanym folderze, stwórz nowy o nazwie np. "build"
- W nowym folderze uruchom komendę: "cmake -D BUILD_SHARED_LIBS=OFF .." . Wyświetlenie na końcu  "ant: NO" lub "JNI: NO" informuje o niepowodzeniu operacji ze względu na niepoprawną instalację biblioteki ant lub języka Java
- W przypadku powodzenia poprzedniej operacji, uruchom komendę: "make -j8"
- Zaimportuj projekt znajdujący się w folderze REST-Server przy użyciu programu Intellij IDEA
- Przejdź do zakładki File -> Project Structure -> Libraries, gdzie należy dodać bibliotekę nazywając ją  "opencv-411" oraz wskazując na ścieżkę "../{stworzony folder}/bin/opencv-411.jar". W przypadku istnienia pola o tej nazwie, należy tylko zmienić widniejącą ścieżkę.

Aplikacja mobilna powinna działać od razu po zaimportowaniu programu znajdującego się w folderze AndroidCV, jednak ze względu na częste komplikacje, przedstawiony został schemat dodawania biblioteki do projektu:
- Po zaimportowaniu projektu, rozpakuj plik "opencv-4.1.1-android-sdk.zip''.
- Zaimportuj moduł OpenCV w aplikacji AndroidStudio przechodząc do zakładki File -> New -> Import Module... , gdzie należy wybrać ścieżkę wskazującą na folder ../opencv-4.1.1-android-sdk/sdk/java
- W zakładce File -> Project Structure -> Dependencies dla pola app, dodaj dodany moduł jako Module dependency
-  Jeśli pod ścieżką "../AndroidCV/app/src/main" nie znajduje się folder o nazwie jniLibs, należy go dodać i umieścić w nim pliki spod ścieżki "../opencv-4.1.1-android-sdk/OpenCV-android-sdk/sdk/native/libs"

W przypadku tworzenia nowego projektu z biblioteką OpenCV, należy pamiętać, że przed użyciem jakiejkolwiek operacji z biblioteki, trzeba ją najpierw załadować. Odbywa się to poprzez uruchomienie kodu System.loadLibrary(Core.NATIVE_LIBRARY_NAME). Wystarczy to zrobić raz, przy starcie aplikacji.
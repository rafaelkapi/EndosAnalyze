 
 
 
 
 # EndosAnalyze
 
 

<img src="https://raw.githubusercontent.com/rafaelkapi/EndosAnalyze/main/screenshot.png" height="600" />


## About this Project

This app has a special connection with me,
because it was through him that I had my first contact with Android mobile programming,
which fascinated me and motivated me to become a professional in the field.

This app was developed for my Control and Automation Engineering CCW(completion of course work), 
its function is to test and promote diagnostics of [rigid endoscopes](https://pt.wikipedia.org/wiki/Endoscopia), widely used in micro surgeries.

## Why?

This project is part of my personal portfolio, so, I'll be happy if you could provide me any feedback about the project,
code, structure or anything that you can report that could make me a better developer!

Email-me: rafaelcapgomes@gmail.com

Connect with me at [LinkedIn](https://www.linkedin.com/in/rafael-araujo-206819181).

Also, you can use this Project as you wish, be for study, be for make improvements or earn money with it!

It's free!

## Installers

If you want to test the App in the Production mode, the installers are listed below:

[Android .apk installer](https://drive.google.com/file/d/1R0IAHr22SidcfNzRQuhb_p-e1KI4Rzk8/view?usp=sharing)


## Functionalities

- Access the rear camera of the device in a personalized way,
  to make the design of the application more receptive to the user, with the use of [SurfaceView](https://developer.android.com/reference/android/view/SurfaceView).
  
- Take photos and edit them using a dynamic cutout, which locates the useful viewing area of the endoscope,
  as shown below, creates location vectors, which emit the correct coordinates to make the cut.
  
  <img src="https://raw.githubusercontent.com/rafaelkapi/EndosAnalyze/main/com-assint.jpg" height="150" />
  
- Analyzes the image and proposes a diagnosis, shows the user 3 forms of analysis:

  - _Endoscope image_ - Identifies the pixels that contain the defect in the image, 
   scanning the [bitmap](https://developer.android.com/reference/android/graphics/Bitmap), changing the color of those pixels to red, so that the defect is highlighted.
   
  - _Text_ - It locates the pixels of the black color identified as the defective ones,
   through calculations it emits how much the lens has been compromised, and a possible cause of the defect. 
   
  - _Graphically_ - Generates a [luminance histogram](https://www.cambridgeincolour.com/pt-br/tutoriais/histograms2.htm) of the endoscope image.
   


## Built With

- [Android-Studio](https://developer.android.com/studio/preview?hl=pt) - Official native android development IDE


## License

This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/steniowagner/mindCast/blob/master/LICENSE) file for details

 



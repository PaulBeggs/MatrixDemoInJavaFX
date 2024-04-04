# Matrix GUI
## Overview

The Matrix Project, developed in Java and utilizing SceneBuilder for the GUI, is a comprehensive application designed for matrix operations and visualizations. This project offers a user-friendly interface to perform a variety of matrix calculations, including but not limited to, REF/RREF, inverting, multiplication, and determinant calculation. The use of SceneBuilder ensures a responsive and intuitive design, making complex matrix operations accessible to users of all levels.

## Features

- Matrix Operations: Perform basic operations like addition, subtraction, and multiplication. Advanced operations such as determinant calculation and matrix inversion are also supported.
- Interactive GUI: Developed with SceneBuilder, the GUI provides an intuitive and easy-to-navigate interface.
- Custom Matrix Input: Users can input custom matrices for operations, offering flexibility in calculations.
- Real-time Calculations: See results of operations in real-time, enhancing the user experience and understanding of matrix concepts.
- Error Handling: Includes robust error handling for invalid matrix operations and inputs, ensuring the application remains stable and user-friendly.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
### Prerequisites

Ensure you have the following installed (*refer to Installation for additional help.*):


- Java JDK 8 or newer
- SceneBuilder (compatible with your Java version)
- An IDE that supports JavaFX (e.g., IntelliJ IDEA, Eclipse)

### Installation

I use IntelliJ and Windows, so for installation, I will walk through how to get the project going using **Windows**. 

1. **If you haven't already, download IntelliJ**:
	1. Go to [IntelliJ Download Page](https://www.jetbrains.com/idea/download/download-thanks.html?platform=windows&code=IIC).
	2. Download either the community version, or the ultimate version based on your needs.
	3. Go to your `downloads` file (or wherever you have your downloads from online go).
	4. Click (or search) `ideaIC-2023.3.6`.
	5. A pop up will appear. 
	6. For the path destination, it should look something like `C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.6`
	7. On the next page, click `.java` under `create associations`. (*Note: advanced programmers would know more about this. Look [here](https://www.jetbrains.com/help/idea/installation-guide.html#snap) for additional help.*)
	8. Select a file name, then download.
2. **Now that you have IntelliJ installed, download the project to get started**:
	1. Go up to the blue box that looks like this:
	![44b967e0866c0a5c8c874387d4358afd.jpg](:/f11465efd68d4f83ada0935cbc120e1e)
		1. Open the dropdown menu and click `download zip`.
		2. Unzip the folder, and place in `IdeaProjects`. (This folder should be automatically downloaded with the installation of IntelliJ.
		3. Open IntelliJ. You should see a menu like this:
			![First Menu for IntelliJ.png](:/4fcd4d4918b44b129e60500c72e9fb25)
		4. Click `Open`. 
		5. Navigate to your `IdeaProjects` folder, and select the file named `MatrixDemoInJavaFX-master`. (You can keep or change this file name to whatever you want.) 


3. **To get started on the project**:
	1. Click on the gear next to the minimize button. 
	2. Click `Project Structure...`. You should be presented with a menu that looks like the picture below. 
	3. Notice the underlined `+`. Click that button and proceed to download the JDK by using the options that are already filled in for you.
	![Project Structure.png](:/784b57239d3e46a48e479d8b22b0dacf)
	4. Click `Apply`, and now configure your JDK at the top of the screen. 
	5. There may be a yellow warning that says your JDK is not configured. 
		* Simply restart your IDE and wait. This is a large project, so indexing takes some time.
4. **Install SceneBuilder**:
	1. Go to [this link](https://gluonhq.com/products/scene-builder/#download).
	2. Scroll down to 'Download Scene Builder'.
	3. Click the first option, or whatever is relevant to your system.
	4. Open the installer from the folder you selected.
	5. Follow the prompts.
5. **Add JavaFX to the project:**
	1. Download the JavaFX package from Gluon using this [link](https://gluonhq.com/products/javafx/).
	2. Scroll down until you see the download for Windows. 
		* Be sure to download the SDK version.
	3. Open IntelliJ and navigate to the project. 
	4. Click the 4 horizontal lines at the top left of the page (next to the IntelliJ icon) to access the `file` dropdown.
	5. Go to `Project Structure...` once again. 
	6. Click the `Libraries` tab, and click the same `+` as before. 
	7. Click `From Java`.
	8. Search for `javafx-sdk-20.0.2`
	9. Click the arrow to expand the dropdown menu.
	10. Go to `lib` and highlight the following, as shown in the picture below:
![JavaFX Library.png](:/84432a9b883a49928fc05b339c4d9e60)		
	11. Select `OK` and `apply`. 
	12. *Note this may take a couple of tries. Consider trouble shooting sources in case you cannot get the library added properly. I consistently have problems adding this library, so don't be discouraged if it doesn't work the first time.*
6. **Add JUnit to the project:**
	1. From the prior step, follow the same procedure until sub-step 3. 
	2. This time, do not click `From Java`. Instead, click `From Maven`.
	3. Type `JUnit` and the magnifying class next to the text box.
	4. Scroll and search for the module, `junit:junit:4.13.2`.
	5. Add to your project and close the window.

## Usage

To use the application, follow these steps:

1. In the `Project` tab, navigate to the `MatrixApp` class. It is located in the project hierarchy as shown:
![MatrixApp Destination.png](:/97f506ead6254962930a96a81a757ae1)
2. On line 16, click the green button to start the application. 

## Acknowledgments

I want to thank Dr. Gabriel Ferrer for initially teaching me how to set up IntelliJ and SceneBuilder.

Additionally, I'd like to thank Professor Lars Seme for teaching me the Linear Algebra methods that are employed in this program. 

Without their guidance, I would not be able to make this project. 

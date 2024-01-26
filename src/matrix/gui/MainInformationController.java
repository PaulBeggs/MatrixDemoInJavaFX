package matrix.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MainInformationController {
    @FXML
    TextFlow textFlow;
    @FXML
    ScrollPane scrollPane;
    @FXML
    BorderPane borderPane;
    @FXML
    Slider zoomSlider;

    @FXML
    public void initialize() {
        configureNodes();
        // Main header
        Text mainHeader = new Text("Matrix Graphical User Interface\n");
        mainHeader.getStyleClass().add("main-header");

//    ----------------------------------------------------------------------------------------------
//                                             GENERAL INFO
//    ----------------------------------------------------------------------------------------------

        // Subheader for Main Header
        Text generalInfoHeader = new Text("\n1. General Information\n");
        generalInfoHeader.getStyleClass().add("sub-header");

        //subSubHeader for General Information
        Text subSubHeaderForManipInfo = new Text("\ni. Changes and Matrix Manipulation\n");
        subSubHeaderForManipInfo.getStyleClass().add("sub-sub-header");

        // Regular text for Changes and Matrix Manipulation
        Text manipInfoText = new Text("""
                [*] Any change by the user that is made to the matrix cells will be automatically saved. If you want to close the application and open it again with a different format mode (i.e., fraction to decimal, or decimal to fraction), the matrix will be automatically updated to reflect that preference.
                                 
                [*] There are only certain characters that can be entered into the editable fields.
                    [+] Any numerical value can be entered and saved automatically (Ex: 1, -2, 3, 4.5, or -6.7)
                    [+] Any operator can be entered into the text fields. That includes, "+", "-", "/", "*", "sqrt(...)", "%" (modulo), and "^".
                    [+] Any operation that requires processing will have to be followed by pressing the "Enter" key.
                    [+] Note that the calculator can have bugs, as all of the operators had to be hand implemented (refer to the class, "ExpressionEvaluator," for additional information).
                    [+] If you leave any spaces between numbers, the program will concatenate the separated numbers, and proceed with calculating the given prompt. However, this will still throw a syntax error because you should be explicit about your number format.
                    [+] Duplicate operators will be flagged a syntax error, the program will notify you, and change the cell back to its previous valid entry.
                                 
                [*] Operations like inverting the matrix, or finding its triangular form, its echelon form, or its reduced echelon form will not be saved to the main matrix instance. If you would like to save one of these changed matrices, simply save the matrix (refer to "Saving and Loading").
                                 
                [*] If you are concerned that a result is not accurate, please double check with an actual, licenced calculator.
                """);
        manipInfoText.getStyleClass().add("regular-text");

        //subSubHeader for General Information
        Text subSubHeaderForSavingInfo = new Text("\nii. Saving and Loading\n");
        subSubHeaderForSavingInfo.getStyleClass().add("sub-sub-header");

        // Regular text for Saving and Loading
        Text savingInfoText = new Text("""
                 [*] Click "Save" to save the matrix instance that you are working with. Note that you will only be able to save it as a default matrix at this stage.
                 
                 [*] Click "Load" to load a specific matrix. The matrix that is loaded in will fit the number format that you have set as preference.
                """);
        savingInfoText.getStyleClass().add("regular-text");

        // fin.

//    ----------------------------------------------------------------------------------------------
//                                                MATRIX
//    ----------------------------------------------------------------------------------------------

        // Subheader for Main Header
        Text matrixHeader = new Text("\n2. Matrix Window\n");
        matrixHeader.getStyleClass().add("sub-header");

        //subSubHeader for Matrix Scene
        Text subSubHeaderForMatrixLeft = new Text("\ni. Left Side\n");
        subSubHeaderForMatrixLeft.getStyleClass().add("sub-sub-header");

        // Regular text for Left Side
        Text leftMatrixText = new Text("""
                [*] Use the "Rows" and "Columns" fields to set the dimensions of your matrix. The matrix can theoretically be any size, but the program is designed for practical dimensions. There may be bugs with huge dimensions.
                    [+] You may only use positive integers in these fields for obvious reasons.
                                 
                [*] Click "Generate Matrix" to produce a fresh matrix with randomized values. The cells are editable; use the "Tab" key to navigate through each cell and add an entry.
                                 
                [*] Click "Identity" to change the matrix instance into an identity matrix with the same dimensions.
                    Note a few things:
                    [+] When you generate your first matrix, the program will automatically save an identity copy. You can use this matrix with later calculations, and it is retrievable from the save folder, "matrices", as "identity.txt".
                    [+] If the matrix is not square, the program will still change the matrix to have 1s along the diagonal, and 0s for all other entries.
                                 
                [*] Click "Transpose" to switch the rows with the columns.
                """);
        leftMatrixText.getStyleClass().add("regular-text");

        //subSubHeader for Matrix Scene
        Text subSubHeaderForMatrixRight = new Text("\nii. Right Side\n");
        subSubHeaderForMatrixRight.getStyleClass().add("sub-sub-header");

        // Regular text for Right Side
        Text rightMatrixText = new Text("""
                [*] Use the "Target Row" field to select the row that you would like to have row operations done on.
                                
                [*] Use the "Source Row" field to select the row that you would like to use as the source for row operations.
                                
                [*] Use the "Multiplier" field to select a value you would like to apply to the source row for row addition (you can use operators like '*', 'sqrt(...)', '^', etc. in this field like how you can with the matrix cells).
                                
                [*] For the dropdown menu, please refer to "iii. Elementary Row Operations" for important information as to how these rows are used during operations!
                                
                [*] Click the "Compute" button to preform the selected operation (selected underneath the button) for the rows.
                                
                [*] Click the "Operations Summary" button to get a record of the operations that you preformed on the matrix. It will mark if you switched or multiplied row(s).
                                
                [*] Click the "Clear Summary" button to set the Operations Summary record back to empty. This gives you a clean slate.
                """);
        rightMatrixText.getStyleClass().add("regular-text");

        //subSubHeader for the Matrix Scene
        Text subSubHeaderForTheMatrixOperations = new Text("\niii. Elementary Row Operations\n");
        subSubHeaderForTheMatrixOperations.getStyleClass().add("sub-sub-header");

        // Regular text for Operations
        Text matrixOperationsText = new Text("""
                [*] Replacement: Replace one row by the sum of itself and a multiple of another row.
                    [+] I will call the "row to be replaced" the "Target Row." This is the row that will have a scaled row added to it.
                    [+] The row to be scaled is the "Source Row." This is what will have a multiplier applied to it, and added to the Target Row.
                                
                [*] Interchange: Interchange two rows.
                                
                [*] Scaling: Multiply all entries in a row by a nonzero constant.
                    [+] When scaling, the only row that will be affected is the Target Row.
                    [+] All entries in the row will be multiplied by the "Multiplier" scalar.
                """);
        matrixOperationsText.getStyleClass().add("regular-text");


//    ----------------------------------------------------------------------------------------------
//                                            MULTIPLICATION
//    ----------------------------------------------------------------------------------------------

        // Subheader for Main Header
        Text multiplicationHeader = new Text("\n3. Multiplication Window\n");
        multiplicationHeader.getStyleClass().add("sub-header");

        // Sub-subheader for Multiplication Window
        Text subSubHeaderForMatrixProduct = new Text("\ni. \n");
        subSubHeaderForMatrixProduct.getStyleClass().add("sub-sub-header");

        // Regular text for Matrix Product
        Text matrixProductText = new Text("""
                                
                """);
        matrixProductText.getStyleClass().add("regular-text");

        // Sub-subheader for Multiplication
        Text subSubHeaderForProductProperties = new Text("\nii. \n");
        subSubHeaderForProductProperties.getStyleClass().add("sub-sub-header");

        // Regular text for ...
        Text productPropertiesText = new Text("""
                                
                """);
        productPropertiesText.getStyleClass().add("regular-text");

//    ---------------------------------------------------------------------------------------------
//                                                SOLVE
//    ---------------------------------------------------------------------------------------------

        // Subheader for Main Header
        Text solveHeader = new Text("\n4. Solve Window\n");
        solveHeader.getStyleClass().add("sub-header");

        // Sub-subheader for Solve Window
        Text subSubHeaderForSolveMethod = new Text("\ni. \n");
        subSubHeaderForSolveMethod.getStyleClass().add("sub-sub-header");

        // Regular text for ...
        Text solveMethodText = new Text("""
                                
                """);
        solveMethodText.getStyleClass().add("regular-text");

        // Sub-subheader for Solve Window
        Text subSubHeaderForSolveResults = new Text("\nii. \n");
        subSubHeaderForSolveResults.getStyleClass().add("sub-sub-header");

        // Regular text for ...
        Text solveResultsText = new Text("""
                                
                """);
        solveResultsText.getStyleClass().add("regular-text");

//    ----------------------------------------------------------------------------------------------
//                                             DETERMINANT
//    ----------------------------------------------------------------------------------------------

        // Subheader for Determinant Scene
        Text determinantHeader = new Text("\n5. Determinant Window\n");
        determinantHeader.getStyleClass().add("sub-header");

        // Sub-subheader for Determinant Scene
        Text subSubHeaderForDeter = new Text("\ni. Detailed Instructions\n");
        subSubHeaderForDeter.getStyleClass().add("sub-sub-header");

        // Regular text for Determinant Scene
        Text regularTextForDeter = new Text("Here are some detailed instructions...\n");
        regularTextForDeter.getStyleClass().add("regular-text");

//    ---------------------------------------------------------------------------------------------
//                                               INVERSE
//    ---------------------------------------------------------------------------------------------

        // Subheader for Main Header
        Text inverseHeader = new Text("\n6. Inverse Window\n");
        inverseHeader.getStyleClass().add("sub-header");

        // Sub-subheader for Inverse Window
        Text subSubHeaderForInverseCalc = new Text("\ni. \n");
        subSubHeaderForInverseCalc.getStyleClass().add("sub-sub-header");

        // Regular text for ...
        Text inverseCalcText = new Text("""
                                
                """);
        inverseCalcText.getStyleClass().add("regular-text");

        // Sub-subheader for Inverse Window
        Text subSubHeaderForInverseApplications = new Text("\nii. \n");
        subSubHeaderForInverseApplications.getStyleClass().add("sub-sub-header");

        // Regular text for ...
        Text inverseApplicationsText = new Text("""
                                
                """);
        inverseApplicationsText.getStyleClass().add("regular-text");

//    ---------------------------------------------------------------------------------------------
//                                                BASES
//    ---------------------------------------------------------------------------------------------

        // Subheader for Main Header
        Text basesHeader = new Text("\n7. Bases Window\n");
        basesHeader.getStyleClass().add("sub-header");

        // Sub-subheader for Basis Window
        Text subSubHeaderForBasisComp = new Text("\ni. \n");
        subSubHeaderForBasisComp.getStyleClass().add("sub-sub-header");

        // Regular text for ...
        Text basisCompText = new Text("""
                                
                """);
        basisCompText.getStyleClass().add("regular-text");

        // Sub-subheader for Basis Window
        Text subSubHeaderForBasisConcepts = new Text("\nii. \n");
        subSubHeaderForBasisConcepts.getStyleClass().add("sub-sub-header");

        // Regular text for ...
        Text basisConceptsText = new Text("""
                                
                """);
        basisConceptsText.getStyleClass().add("regular-text");

//    ---------------------------------------------------------------------------------------------
//                                                EIGEN
//    ---------------------------------------------------------------------------------------------

        // Subheader for Main Header
        Text eigenHeader = new Text("\n8. Eigen Window\n");
        eigenHeader.getStyleClass().add("sub-header");

        // Sub-subheader for Eigen Window
        Text subSubHeaderForEigenvalues = new Text("\ni. Eigenvalues\n");
        subSubHeaderForEigenvalues.getStyleClass().add("sub-sub-header");

        // Regular text for ...
        Text eigenvaluesText = new Text("""
                                
                """);
        eigenvaluesText.getStyleClass().add("regular-text");

        // Sub-subheader for Eigen Window
        Text subSubHeaderForEigenvectors = new Text("\nii. Eigenvectors\n");
        subSubHeaderForEigenvectors.getStyleClass().add("sub-sub-header");

        // Regular text for ...
        Text eigenvectorsText = new Text("""
                                
                """);
        eigenvectorsText.getStyleClass().add("regular-text");

//    ---------------------------------------------------------------------------------------------
//                                               CONFIG
//    ---------------------------------------------------------------------------------------------

        // Add all text elements to the TextFlow
        textFlow.getStyleClass().add("text-flow");
        textFlow.getChildren().addAll(
                mainHeader,
                generalInfoHeader, subSubHeaderForManipInfo, manipInfoText, subSubHeaderForSavingInfo, savingInfoText,
                matrixHeader, subSubHeaderForMatrixLeft, leftMatrixText, subSubHeaderForMatrixRight, rightMatrixText, subSubHeaderForTheMatrixOperations, matrixOperationsText,
                multiplicationHeader, subSubHeaderForMatrixProduct, matrixProductText, subSubHeaderForProductProperties, productPropertiesText,
                solveHeader, subSubHeaderForSolveMethod, solveMethodText, subSubHeaderForSolveResults, solveResultsText,
                determinantHeader, subSubHeaderForDeter, regularTextForDeter,
                inverseHeader, subSubHeaderForInverseCalc, inverseCalcText, subSubHeaderForInverseApplications, inverseApplicationsText,
                basesHeader, subSubHeaderForBasisComp, basisCompText, subSubHeaderForBasisConcepts, basisConceptsText,
                eigenHeader, subSubHeaderForEigenvalues, eigenvaluesText, subSubHeaderForEigenvectors, eigenvectorsText
        );

        scrollPane.getStyleClass().add("scrollPane");
        scrollPane.setVvalue(0);
    }

    private void configureNodes() {
        scrollPane.setContent(textFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
    }
}

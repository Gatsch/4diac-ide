package org.eclipse.fordiac.ide.refactor.handlers;



import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fordiac.ide.application.editparts.ConnectionEditPart;
import org.eclipse.fordiac.ide.application.wizards.AbstractSaveAsWizard;
import org.eclipse.fordiac.ide.model.data.DataFactory;
import org.eclipse.fordiac.ide.model.data.DataType;
import org.eclipse.fordiac.ide.model.data.StructuredType;
import org.eclipse.fordiac.ide.model.helpers.InterfaceListCopier;
import org.eclipse.fordiac.ide.model.libraryElement.Connection;
import org.eclipse.fordiac.ide.model.libraryElement.DataConnection;
import org.eclipse.fordiac.ide.model.libraryElement.Event;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetworkElement;
import org.eclipse.fordiac.ide.model.libraryElement.FBType;
import org.eclipse.fordiac.ide.model.libraryElement.IInterfaceElement;
import org.eclipse.fordiac.ide.model.libraryElement.LibraryElement;
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration;
import org.eclipse.fordiac.ide.model.typelibrary.TypeEntry;
import org.eclipse.fordiac.ide.model.typelibrary.TypeLibraryManager;
import org.eclipse.fordiac.ide.model.typelibrary.TypeLibraryTags;
import org.eclipse.fordiac.ide.refactor.commands.ConnectionsToStructCommand;
import org.eclipse.fordiac.ide.typemanagement.preferences.TypeManagementPreferencesHelper;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.handlers.HandlerUtil;

public class Connections2StructHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		final IEditorPart editor = HandlerUtil.getActiveEditor(event);
		final CommandStack commandStack = editor.getAdapter(CommandStack.class);

		System.out.println(HandlerUtil.getCurrentSelection(event));
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		// final CommandStack commandStack = editor.getAdapter(CommandStack.class);
		// final FBNetworkElement fb = getNetworkElementFromSelectedPins(sel);

		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Object firstElement = selection.getFirstElement();
			List<String> sourceVarNames = selection.stream().map(t -> ((ConnectionEditPart) t).getModel().getSource().getName()).toList();

			List<String> destinationVarNames = selection.stream().map(t -> ((ConnectionEditPart) t).getModel().getDestination().getName()).toList();
			

			List<VarDeclaration> varDecls = selection.stream()
					.map(t -> (VarDeclaration) ((ConnectionEditPart) t).getModel().getSource()).toList();
			List<String> varWiths = varDecls.stream().map(t -> t.getWiths()).flatMap(List::stream).map(t -> ((Event)t.eContainer()).getName()).distinct().toList();

			List<VarDeclaration> varDecld = selection.stream()
					.map(t -> (VarDeclaration) ((ConnectionEditPart) t).getModel().getDestination()).toList();
			List<String> varWithd = varDecld.stream().map(t -> t.getWiths()).flatMap(List::stream).map(t -> ((Event)t.eContainer()).getName()).distinct().toList();

			List<ConnectionEditPart> connections = selection.stream()
					.filter(ConnectionEditPart.class::isInstance)
					.map(ConnectionEditPart.class::cast).toList();
			FBType sourceType = connections.get(0).getModel().getSourceElement().getType();
			FBType destinationType = connections.get(0).getModel().getDestinationElement().getType();			
			
			//TODO: check if cast to VarDeclaration is valid
			List<VarDeclaration> sourceVarDeclarations = connections.stream()
					.map(con -> (VarDeclaration)con.getModel().getSource()).toList();
			List<String> sourceWiths = varDecls.stream()
					.map(var -> var.getWiths())
					.flatMap(List::stream)
					.map(width -> ((Event)width.eContainer()).getName()).distinct().toList();

			List<VarDeclaration> destinationVarDeclarations = connections.stream()
					.map(con -> (VarDeclaration)con.getModel().getDestination()).toList();
			List<String> destinationWiths = varDecld.stream()
					.map(var -> var.getWiths())
					.flatMap(List::stream)
					.map(width -> ((Event)width.eContainer()).getName()).distinct().toList();
			
//			
//			ExtractStructTypeWizard structWizard = new ExtractStructTypeWizard(varDecls,
//					getProject(((ConnectionEditPart) firstElement).getModel().getSourceElement()),
//					Messages.ConvertToStructHandler_Title);
//			final WizardDialog structDialog = new WizardDialog(editor.getSite().getShell(), structWizard);
//			structDialog.create();
//			structDialog.open();
			
			
			
			
			
			class ConnectionsToStructPage extends WizardNewFileCreationPage {
				
				private Text sourceNameText;
				private Text destinationNameText;
				private Button conflictButton;
				
				protected ConnectionsToStructPage(final String pageName, IStructuredSelection selection) {
					super(pageName, selection);
					this.setTitle("Convert Connections to Structured Type");
					this.setDescription("Store new Type in Library. Select the name of the In/Output Vars. Choose how to resolve conflicts");
				}

				@Override
				public void createControl(final Composite parent) {
					super.createControl(parent);
					final Composite composite = (Composite) getControl();
					setErrorMessage(null);
					setMessage(null);
					setControl(composite);
				}
				
				@Override
				protected void createAdvancedControls(Composite parent) {
					final Composite container = new Composite(parent, SWT.NONE);
					GridData containerData = new GridData();
					containerData.horizontalAlignment = GridData.FILL;
					containerData.grabExcessHorizontalSpace = true;
					container.setLayoutData(containerData);
					
					GridLayout gridl = new GridLayout();
					gridl.numColumns = 2;
					gridl.marginWidth = 0;
					container.setLayout(gridl);
					
					final Label sourceNameLabel = new Label(container, NONE);
					sourceNameLabel.setText("Function Block Output Name" + ":");
					sourceNameText = new Text(container, SWT.SINGLE | SWT.BORDER);
					//TODO: wrong ID?
					sourceNameText.addListener(SWT.ERROR, this);
					
					final Label destinationNameLabel = new Label(container, NONE);
					destinationNameLabel.setText("Function Block Input Name" + ":");
					destinationNameText = new Text(container, SWT.BORDER);
					destinationNameText.addListener(SWT.ERROR, this);
					
					GridData textGridData = new GridData();
					textGridData.horizontalAlignment = GridData.FILL;
					textGridData.grabExcessHorizontalSpace = true;
					sourceNameText.setLayoutData(textGridData);
					destinationNameText.setLayoutData(textGridData);
					
					conflictButton = new Button(container, SWT.CHECK);
					conflictButton.setText("Solve conflicts with Multiplexer/Demultiplexer");
					conflictButton.setSelection(true);
					GridData checkboxGridData = new GridData();
					checkboxGridData.horizontalSpan = 2;
					conflictButton.setLayoutData(checkboxGridData);
					super.createAdvancedControls(parent);
				};
				
				public String getSourceName() {
					return sourceNameText.getText();
				}
				
				public String getDestinationName() {
					return destinationNameText.getText();
				}
				
				public boolean getConflictResolution() {
					return conflictButton.getSelection();
				}
				
				protected boolean validatePage() {
					return super.validatePage() && !sourceNameText.getText().isBlank() && !destinationNameText.getText().isBlank();
				};

				public boolean isPageComplete() {
					return super.isPageComplete();
				};

			}
			
			class testwizard extends AbstractSaveAsWizard {
				private ConnectionsToStructPage choosePage;
				private DataType createdType;
				private String sourceName;
				private String destinationName;
				private boolean conflictResolution;
				
				@Override
				public boolean performFinish() {
					final IFile targetFile = getTargetTypeFile();
					final TypeEntry entry = createTypeEntry(targetFile);

					final StructuredType type = DataFactory.eINSTANCE.createStructuredType();
					
					List<VarDeclaration> varDecl = selection.stream()
							.filter(ConnectionEditPart.class::isInstance)
							.map(ConnectionEditPart.class::cast)
							.map(ConnectionEditPart::getModel)
							.map(Connection::getSource)
							.filter(VarDeclaration.class::isInstance)
							.map(VarDeclaration.class::cast).toList();
					
					InterfaceListCopier.copyVarList(type.getMemberVariables(), varDecl, true);

					TypeManagementPreferencesHelper.setupVersionInfo(type);
					String datatypeName = TypeEntry.getTypeNameFromFile(targetFile);
					type.setName(datatypeName);
					try {
						entry.save(type, new NullProgressMonitor());
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					createdType = type;
					sourceName = choosePage.getSourceName();
					destinationName = choosePage.getDestinationName();
					conflictResolution = choosePage.getConflictResolution();
					return true;
				}
				
				public void addPages() {
					choosePage = new ConnectionsToStructPage("Connections to Struct", selection);
					addPage(choosePage);
				};
				
				public DataType getCreatedDataType() {
					return createdType;
				}
				
				public String getSourceName() {
					return sourceName;
				}
				
				public String getDestinationName() {
					return destinationName;
				}
				
				public boolean getConflictResolution() {
					return conflictResolution;
				}
				
				private TypeEntry createTypeEntry(final IFile targetTypeFile) {
//					final EObject root = EcoreUtil.getRootContainer((EObject)selection.getFirstElement());
//					if (root instanceof final LibraryElement libEl) {
//						
//						return TypeLibraryManager.INSTANCE.getTypeLibrary(libEl.getTypeLibrary().getProject()).createTypeEntry(targetTypeFile);
//					}
//					return null;
					return TypeLibraryManager.INSTANCE.getTypeLibrary(((ConnectionEditPart)selection.getFirstElement()).getModel().getSourceElement().getTypeLibrary().getProject()).createTypeEntry(targetTypeFile);
				}


				@Override
				protected boolean askOverwrite() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public IFile getTargetTypeFile() {
					final Path path = new Path(choosePage.getContainerFullPath() + File.separator + choosePage.getFileName()
							+ TypeLibraryTags.DATA_TYPE_FILE_ENDING_WITH_DOT);
					return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				}
				
			}
			
			testwizard wizard = new testwizard();
			final WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().getActiveEditor().getSite().getShell(), wizard);
			final ActionListener alist = e -> dialog.updateButtons();
			wizard.setWindowTitle("Connections to Struct");
			dialog.create();


			if (dialog.open() == 0) {
				
				
				Map<String, String> replacableConMap = new HashMap<String, String>();
				connections.stream().forEach(con -> replacableConMap.put(con.getModel().getSource().getName(), con.getModel().getDestination().getName()));

				commandStack.execute(new ConnectionsToStructCommand(sourceType, destinationType, wizard.getCreatedDataType(), wizard.getSourceName(), wizard.getDestinationName(), replacableConMap, wizard.getConflictResolution()));
			}
			
			
			
			//TODO: what if two destinations?

			
//			Map<FBNetworkElement, List<FBNetworkElement>> structconnectionMap = new HashMap<FBNetworkElement, List<FBNetworkElement>>();
//			
//			
//			//Edit FBs
//			CompoundCommand editFBsCommand = new CompoundCommand();
//			replacableConMap.keySet().forEach(sourceVar -> editFBsCommand.add(new DeleteInterfaceCommand(sourceType.getInterfaceList().getOutput(sourceVar))));
//			replacableConMap.values().forEach(destinationVar -> editFBsCommand.add(new DeleteInterfaceCommand(destinationType.getInterfaceList().getInput(destinationVar))));
//			
//			//TODO: get name from user
//			String name = "teststruct";
//			CreateInterfaceElementCommand sourceStruct = new CreateInterfaceElementCommand(sourceType.getTypeLibrary().getDataTypeLibrary().getType(structWizard.getDatatypeName()), name, sourceType.getInterfaceList(), false, (int)sourceType.getInterfaceList().getOutputs().count());
//			CreateInterfaceElementCommand destinationStruct = new CreateInterfaceElementCommand(destinationType.getTypeLibrary().getDataTypeLibrary().getType(structWizard.getDatatypeName()), name, destinationType.getInterfaceList(), true, (int)destinationType.getInterfaceList().getInputs().count());
//			editFBsCommand.add(sourceStruct);
//			editFBsCommand.add(destinationStruct);
//			commandStack.execute(editFBsCommand);
//			
//			//Recreate event bindings
//			CompoundCommand createWidths = new CompoundCommand();
//			sourceWiths.forEach(width -> createWidths.add(new WithCreateCommand(sourceType.getInterfaceList().getEvent(width),(VarDeclaration) sourceStruct.getCreatedElement())));
//			destinationWiths.forEach(width -> createWidths.add(new WithCreateCommand(destinationType.getInterfaceList().getEvent(width),(VarDeclaration) destinationStruct.getCreatedElement())));
//			commandStack.execute(createWidths);
//			
//			
//			final String[] labels = {"Save and Update", //Messages.FBTypeEditor_AlteringButton_SaveAndUpdate, // Messages.StructAlteringButton_SaveAs,
//						SWT.getMessage("SWT_Cancel") }; //$NON-NLS-1$
//			//FBTypeEditor.Messages
//			FBTypeUpdateDialog<TypeEntry> fbSaveDialog = new FBTypeUpdateDialog<TypeEntry>(null, "Function Block Editor", null, "", //$NON-NLS-1$
//					MessageDialog.NONE, labels, 0, new FBTypeEntryDataHandler(sourceType.getTypeEntry()));
//
//			BlockTypeInstanceSearch sourceSearch = new BlockTypeInstanceSearch(sourceType.getTypeEntry());
//			BlockTypeInstanceSearch destinationSearch = new BlockTypeInstanceSearch(destinationType.getTypeEntry());
//			
//			//TODO: same dialog as for FBTypeEditor
//			//Open update dialog
//			switch (fbSaveDialog.open()) {
//			case 0:
//				AbstractLiveSearchContext.save(sourceType.getTypeEntry());
//				AbstractLiveSearchContext.save(destinationType.getTypeEntry());
//				
//				List<FBNetworkElement> sourceSearchResults = sourceSearch.performSearch().stream().map(FBNetworkElement.class::cast).toList();
//				List<FBNetworkElement> destinationSearchResults = destinationSearch.performSearch().stream().map(FBNetworkElement.class::cast).toList();
//				
//				Map <FBNetworkElement, FBNetworkElement> demuxMap = new HashMap<FBNetworkElement, FBNetworkElement>();
//				Map <FBNetworkElement, FBNetworkElement> muxMap = new HashMap<FBNetworkElement, FBNetworkElement>();
//				
//				//Update FBs
//				CompoundCommand updateCommands = new CompoundCommand();
//				sourceSearchResults.stream()
//				.forEach(fbnelement -> updateCommands.add(new UpdateFBTypeCommand(fbnelement)));;
//				destinationSearchResults.stream()
//				.forEach(fbnelement -> updateCommands.add(new UpdateFBTypeCommand(fbnelement)));
//				commandStack.execute(updateCommands);
//				
//				destinationSearchResults = destinationSearch.performSearch().stream().map(FBNetworkElement.class::cast).toList();
//				sourceSearchResults = sourceSearch.performSearch().stream().map(FBNetworkElement.class::cast).toList();
//				
//				CompoundCommand muxCreateCommand = new CompoundCommand();
//				
//				Map<FBNetworkElement, FBNetworkElement> structConnectionMap = new HashMap<FBNetworkElement, FBNetworkElement>();
//				
//				//Create map between correct connected FBs and create MUX
//				destinationSearchResults.stream().forEach(instance -> {
//					instance.getInterface().getErrorMarker().stream().flatMap(err -> err.getInputConnections().stream());
//					
//					List<Connection> cons = instance.getInterface().getErrorMarker().stream()
//							.flatMap(err -> err.getInputConnections().stream())
//							.filter(con -> replacableConMap.containsKey(con.getSource().getName()) 
//									&& replacableConMap.get(con.getSource().getName()).equals(con.getDestination().getName()) 
//									&& con.getSourceElement().getType().getName().equals(sourceType.getName()))
//							.toList();
//					
//					if (cons.stream().map(con -> con.getSourceElement()).distinct().count() == 1 
//							&& cons.stream().count() == replacableConMap.size()) {
//						structConnectionMap.put(instance, instance.getInterface().getErrorMarker().stream()
//								.flatMap(err -> err.getInputConnections().stream())
//								.filter(con -> replacableConMap.containsValue(con.getDestination().getName()))
//								.findFirst().get().getSourceElement());
//					} else {
//						FBCreateCommand muxcreate = new FBCreateCommand(instance.getTypeLibrary().getFBTypeEntry("STRUCT_MUX"), instance.getFbNetwork(), 0,0);//(int)(instance.getPosition().getX()+10), (int)instance.getPosition().getY());
//						muxCreateCommand.add(muxcreate);
//						commandStack.execute(muxcreate);
//						ChangeStructCommand changeStruct = new ChangeStructCommand((StructManipulator)muxcreate.getElement(), muxcreate.getElement().getTypeLibrary().getDataTypeLibrary().getType(structWizard.getDatatypeName()));
//						muxCreateCommand.add(changeStruct);
//						commandStack.execute(changeStruct);
//						muxMap.put(instance, changeStruct.getNewElement());
//					}
//				});
//				
//				//Create DEMUX if needed
//				sourceSearchResults.stream().forEach(instance -> {
//					if (instance.getInterface().getErrorMarker().stream()
//							.flatMap(err -> err.getOutputConnections().stream())
//							.filter(con -> !(structConnectionMap.containsKey(con.getDestinationElement())
//									&& structConnectionMap.get(con.getDestinationElement()).equals(instance)
//									&& replacableConMap.containsKey(con.getSource().getName())
//									&& replacableConMap.get(con.getSource().getName()).equals(con.getDestination().getName())))
//							.count() != 0) {
//						FBCreateCommand muxcreate = new FBCreateCommand(instance.getTypeLibrary().getFBTypeEntry("STRUCT_DEMUX"), instance.getFbNetwork(), 0,0);//(int)(instance.getPosition().getX()+10), (int)instance.getPosition().getY());
//						muxCreateCommand.add(muxcreate);
//						commandStack.execute(muxcreate);
//						ChangeStructCommand changeStruct = new ChangeStructCommand((StructManipulator)muxcreate.getElement(), muxcreate.getElement().getTypeLibrary().getDataTypeLibrary().getType(structWizard.getDatatypeName()));
//						muxCreateCommand.add(changeStruct);
//						commandStack.execute(changeStruct);
//						demuxMap.put(instance, changeStruct.getNewElement());
//					}
//				});
//				//commandstack.execute(MuxCreateCommand) not needed
//				
//				//Edit destination connections
//				CompoundCommand destinationConnectionEditCommand = new CompoundCommand();
//				destinationSearchResults.stream().forEach(instance -> {
//					if (structConnectionMap.containsKey(instance)) {
//						StructDataConnectionCreateCommand structCon = new StructDataConnectionCreateCommand(instance.getFbNetwork());
//						structCon.setDestination(instance.getInput(destinationStruct.getCreatedElement().getName()));
//						structCon.setSource(structConnectionMap.get(instance).getOutput(sourceStruct.getCreatedElement().getName()));
//						destinationConnectionEditCommand.add(structCon);
//						instance.getInterface().getErrorMarker().stream()
//						.flatMap(err -> err.getInputConnections().stream())
//						.filter(con -> replacableConMap.containsValue(con.getDestination().getName()))
//						.forEach(con -> destinationConnectionEditCommand.add(new DeleteConnectionCommand(con)));
//					} else {
//						StructDataConnectionCreateCommand structCon = new StructDataConnectionCreateCommand(instance.getFbNetwork());
//						structCon.setDestination(instance.getInput(destinationStruct.getCreatedElement().getName()));
//						structCon.setSource(muxMap.get(instance).getOutput("OUT"));
//						destinationConnectionEditCommand.add(structCon);
//						instance.getInterface().getErrorMarker().stream()
//						.flatMap(err -> err.getInputConnections().stream())
//						.filter(con -> replacableConMap.containsValue(con.getDestination().getName()))
//						.forEach(con -> {
//							String var = replacableConMap.entrySet().stream().filter(entry -> entry.getValue().equals(con.getDestination().getName())).findAny().get().getKey();
//							destinationConnectionEditCommand.add(new ReconnectDataConnectionCommand(con, false, muxMap.get(instance).getInput(var), instance.getFbNetwork()));
//						});
//					}
//				});
//				commandStack.execute(destinationConnectionEditCommand);
//				
//				//Edit source connections
//				CompoundCommand sourceConnectionEditCommand = new CompoundCommand();
//				sourceSearchResults.stream().forEach(instance -> {
//					if (!structConnectionMap.containsValue(instance)) {
//						StructDataConnectionCreateCommand structCon = new StructDataConnectionCreateCommand(instance.getFbNetwork());
//						structCon.setDestination(demuxMap.get(instance).getInput("IN"));
//						structCon.setSource(instance.getOutput(sourceStruct.getCreatedElement().getName()));
//						sourceConnectionEditCommand.add(structCon);
//						instance.getInterface().getErrorMarker().stream()
//						.flatMap(err -> err.getOutputConnections().stream())
//						.filter(con -> replacableConMap.containsKey(con.getSource().getName()))
//						.forEach(con -> sourceConnectionEditCommand.add(new ReconnectDataConnectionCommand(con, true, demuxMap.get(instance).getOutput(con.getSource().getName()), instance.getFbNetwork())));
//					}
//				});
//				commandStack.execute(sourceConnectionEditCommand);
//				break;
//			case 1:
//				MessageDialog.openInformation(null, "Function Block Editor", "The changes have not been saved!");
//				break;
//			}
			
		}
		
		return null;
	}

	private static IProject getProject(final FBNetworkElement fb) {
		final EObject root = EcoreUtil.getRootContainer(fb);
		if (root instanceof final LibraryElement libEl) {
			return libEl.getTypeLibrary().getProject();
		}
		return null;
	}

	@Override
	public void setEnabled(final Object evaluationContext) {
		final IWorkbenchPart part = (IWorkbenchPart) HandlerUtil.getVariable(evaluationContext,
				ISources.ACTIVE_EDITOR_NAME);
		setBaseEnabled(isSelectedConnection());
		// setBaseEnabled((part != null) && (part.getAdapter(FBNetwork.class) != null));

	}

	private static boolean isSelectedConnection() {
		// final ISelection selection = HandlerUtil.selection
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IInterfaceElement src;
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Optional opt = selection.stream().filter(sel -> !(sel instanceof ConnectionEditPart con && con.getModel() instanceof DataConnection)).findAny();
			if (opt.isEmpty()) {
				// selection.stream().forEach(t ->
				// ((ConnectionEditPart)t).getModel().setBrokenConnection(true));
				selection.stream()
						.forEach(t -> System.out.println(((ConnectionEditPart) t).getModel().getSource().getClass()));
				return selection.stream().map(t -> ((ConnectionEditPart) t).getModel().getSourceElement()).distinct()
						.count() == 1
						&& selection.stream().map(t -> ((ConnectionEditPart) t).getModel().getDestinationElement())
								.distinct().count() == 1;
			}
		}
		return false;
	}
}

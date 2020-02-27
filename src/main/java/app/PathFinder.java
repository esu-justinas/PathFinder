package app;

import app.model.CityTableModel;
import app.model.RoadTableModel;
import app.model.WarehouseTableModel;
import core.*;
import csv.CSVFileManager;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class PathFinder {
    private static ArrayList<City> cities;
    private static ArrayList<Road> roads;
    private static ArrayList<Warehouse> warehouses;

    private CityTableModel cityModel;
    private static WarehouseTableModel warehouseModel;
    private static RoadTableModel roadModel;

    private JFrame frame;

    private JPanel bodyPanel;

    private JTextField citySourceField;
    private JTextField warehouseSourceField;
    private JTextField roadSourceField;

    private JTable cityTable;
    private JTable warehouseTable;
    private JTable roadTable;

    private JButton saveButton;

    private JButton deleteCityButton;
    private JButton deleteWarehouseButton;
    private JButton deleteRoadButton;

    private JComboBox<City> cityComboBoxWarehouse;

    private JComboBox<City> originCityComboBox;
    private JComboBox<City> adjacentCityComboBox;
    private JComboBox<String> groupComboBox;

    private JComboBox<City> sourceCityComboBox;
    private JComboBox<City> destinationCityComboBox;

    private JTextArea resultArea;

    public static void main(String[] args) {
    	try {
            UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException |
                IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PathFinder window = new PathFinder();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private PathFinder() {
        initialize();
        bodyPanel.setVisible(false);
    }

    private void initialize() {
        cities = new ArrayList<>();
        warehouses = new ArrayList<>();
        roads = new ArrayList<>();

        frame = new JFrame("eFlow2W");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Initializing backgroundPanel and defining constraints for body and header
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        GridBagConstraints headerConstraints = new GridBagConstraints();
        GridBagConstraints bodyConstraints = new GridBagConstraints();

        headerConstraints.fill = GridBagConstraints.HORIZONTAL;
        headerConstraints.weightx = 0.5;
        headerConstraints.gridx = 0;
        headerConstraints.gridy = 0;

        bodyConstraints.fill = GridBagConstraints.BOTH;
        bodyConstraints.weighty = 0.5;
        bodyConstraints.ipady = 50;
        bodyConstraints.gridx = 0;
        bodyConstraints.gridy = 1;

        JPanel headerPanel = createHeaderPanel();

        bodyPanel = createBodyPanel();

        //Adding header and body panels into the main background panel
        backgroundPanel.add(headerPanel, headerConstraints);
        backgroundPanel.add(bodyPanel, bodyConstraints);

        //Adding background panel onto the frame
        frame.getContentPane().add(backgroundPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    /*
     *   Creates and initializes components of header
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel sourcePanel = new JPanel(new GridLayout(0, 2, 0, 10));
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 0, 10));

        JLabel citySourceLabel = new JLabel("City CSV Filename:");
        citySourceField = new JTextField("\\src\\database\\city.csv");
        citySourceField.setPreferredSize(new Dimension(180, 23));

        JLabel warehouseSourceLabel = new JLabel("Warehouse CSV Filename:");
        warehouseSourceField = new JTextField("\\src\\database\\warehouse.csv");
        warehouseSourceField.setPreferredSize(new Dimension(180, 23));

        JLabel roadSourceLabel = new JLabel("Road CSV Filename:");
        roadSourceField = new JTextField("\\src\\database\\road.csv");
        roadSourceField.setPreferredSize(new Dimension(180, 23));

        saveButton = new JButton("SAVE");
        JButton loadButton = new JButton("LOAD");

        saveButton.setPreferredSize(new Dimension(150, 40));
        loadButton.setPreferredSize(new Dimension(150, 40));

        saveButton.setEnabled(false);
        saveButton.setDefaultCapable(true);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveData();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                loadData();
            }
        });

        sourcePanel.add(citySourceLabel);
        sourcePanel.add(citySourceField);
        sourcePanel.add(warehouseSourceLabel);
        sourcePanel.add(warehouseSourceField);
        sourcePanel.add(roadSourceLabel);
        sourcePanel.add(roadSourceField);

        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);

        header.add(sourcePanel);
        header.add(buttonPanel);

        return header;
    }

    //Action performed by SAVE button
    private void saveData() {
        if(!roadSourceField.getText().endsWith(".csv") || !warehouseSourceField.getText().endsWith(".csv") ||
                !citySourceField.getText().endsWith(".csv")) {
            JOptionPane.showMessageDialog(frame, "Error:\nFile has to be in .csv format", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            CSVFileManager.saveRoadFile(roads, warehouses, cities, roadSourceField.getText(),
                    warehouseSourceField.getText(), citySourceField.getText());
        } catch (IOException e){
            JOptionPane.showMessageDialog(frame, "Error:\nThe path to file was not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Action performed by LOAD button
    private void loadData() {
        cities.clear();
        City.resetCount();
        warehouses.clear();
        Warehouse.resetCount();
        roads.clear();
        Road.resetCount();

        try {
            CSVFileManager.readFromFile(cities, warehouses, roads, citySourceField.getText(),
                    warehouseSourceField.getText(), roadSourceField.getText());

            loadWarehousePanelComboBox();
            loadRoadPanelComboBoxes();
            loadGraphPanelComboBoxes();

            saveButton.setEnabled(true);
            bodyPanel.setVisible(true);

            cityModel.fireTableStructureChanged();
            warehouseModel.fireTableStructureChanged();
            roadModel.fireTableStructureChanged();

            deleteRoadButton.setEnabled(false);
            deleteCityButton.setEnabled(false);
            deleteWarehouseButton.setEnabled(false);
        } catch(FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Error:\nOne of the files were not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch(NoSuchElementException e) {
            JOptionPane.showMessageDialog(frame, "Error:\nCheck if files are in correct format and UTF-8 encoding",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     *  Creates and initializes Body panel components
     */
    private JPanel createBodyPanel() {
        JPanel body = new JPanel(new GridLayout(1, 1));
        JTabbedPane tabbedPane = new JTabbedPane();

        //Creating and initializing tabs for tabbed pane
        tabbedPane.addTab("City data", createCityTabPanel());
        tabbedPane.addTab("Warehouse data", createWarehouseTabPanel());
        tabbedPane.addTab("Road data", createRoadTabPanel());
        tabbedPane.addTab("Path finder", createGraphPanel());

        body.add(tabbedPane);

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        return body;
    }

    /*
     *  Creation and initialization of tabbed panels
     */
    private JComponent createCityTabPanel() {
        JPanel cityPanel = new JPanel(new GridLayout(2,1));
        cityPanel.setPreferredSize(new Dimension(740, 390));

        //Initializing table model and table itself
        cityModel = new CityTableModel();
        cityTable = new JTable(cityModel);

        //Configuring city table
        //cityTable.setPreferredScrollableViewportSize(new Dimension(720, 155));
        cityTable.setFillsViewportHeight(true);
        cityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cityTable.getTableHeader().setReorderingAllowed(false);

        //adding mouse listener to allow more complex modifications inside the table
        cityTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);

                int row = cityTable.rowAtPoint(mouseEvent.getPoint());
                int column = cityTable.columnAtPoint(mouseEvent.getPoint());
                int clickCounter = mouseEvent.getClickCount();

                if (row != -1) {
                    deleteCityButton.setEnabled(true);
                }

                if (clickCounter < 2) {
                    return;
                }

                if(column == 3) {
                    City modifiedCity = cities.get(row);
                    if (modifiedCity.isHasWarehouse()){
                        int n = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete a warehouse?",
                                "Delete Warehouse", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                        if (n == JOptionPane.YES_OPTION) {
                            for(Warehouse w : warehouses) {
                                if ( w.getCityId() == modifiedCity.getCityId() ) {
                                    warehouses.remove(w);

                                    warehouseModel.fireTableStructureChanged();
                                    //sets delete warehouse button to disabled, because of structure change
                                    deleteWarehouseButton.setEnabled(false);
                                    break;
                                }
                            }
                            modifiedCity.setHasWarehouse(false);
                            cityModel.fireTableCellUpdated(row, column);

                            //updates comboboxes
                            loadWarehousePanelComboBox();
                            loadGraphPanelComboBoxes();
                        }

                    } else {
                        int n = JOptionPane.showConfirmDialog(frame, "Are you sure you want to add new warehouse?",
                                "New Warehouse", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                        if (n == JOptionPane.YES_OPTION) {
                            //Getting the input from user about new warehouse name
                            String warehouseName = JOptionPane.showInputDialog(frame, "Enter the name of new warehouse:",
                                    "New Warehouse", JOptionPane.PLAIN_MESSAGE);

                            //Adding new warehouse to data set
                            if((warehouseName != null) && (warehouseName.length() > 0)) {
                                warehouses.add(new Warehouse(Warehouse.getCount() + 1, modifiedCity.getCityId(),
                                        warehouseName, modifiedCity.getCityName(), modifiedCity.isHasRoad(),
                                        modifiedCity.getRoads()));

                                modifiedCity.setHasWarehouse(true);
                                warehouseModel.fireTableStructureChanged();
                                cityModel.fireTableCellUpdated(row, column);

                                //updates combo boxes
                                loadWarehousePanelComboBox();
                                loadGraphPanelComboBoxes();

                                //sets delete warehouse button to disabled, because of structure change
                                deleteWarehouseButton.setEnabled(false);
                            }
                        }

                    }
                } else if (column == 4) {          //Column 4 - 'Has Road' column
                    if (cities.get(row).isHasRoad()) {
                        deleteAllRoadsPopup(row);
                    } else {
                        addRoadPopup(row);
                    }
                } else if (column == 5) {   //Column 5 - '# of Roads' column
                    if (mouseEvent.getClickCount() == 2) {
                        editNumberOfRoadsPopup(row);
                    }
                }
            }
        });

        //Putting cityTable into a scroll pane
        JScrollPane scrollPane = new JScrollPane(cityTable);

        cityPanel.add(scrollPane);
        cityPanel.add(createCityManagementPanel());

        return cityPanel;
    }

    private JPanel createCityManagementPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        JPanel rightPanel = new JPanel(new GridLayout(4, 2, 0, 25));
        JPanel leftPanel = new JPanel(new GridLayout(3, 2, 20, 25));

        JLabel cityLabel = new JLabel("City name: ");
        JLabel districtLabel = new JLabel("District name: ");
        JLabel roadLabel = new JLabel("Number of roads: ");
        JLabel warehouseLabel = new JLabel("Warehouse name: ");
        JLabel filler = new JLabel("");

        JTextField cityField = new JTextField();
        JTextField districtField = new JTextField();
        JTextField warehouseField = new JTextField();

        JSpinner roadSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        //roadSpinner.setEditor(new JSpinner.NumberEditor(roadSpinner, "#"));
        JFormattedTextField roadField = ((JSpinner.NumberEditor) roadSpinner.getEditor()).getTextField();
        ((NumberFormatter) roadField.getFormatter()).setAllowsInvalid(false);

        JButton addButton = new JButton("Add city");
        deleteCityButton = new JButton("Delete city");
        deleteCityButton.setEnabled(false);

        JCheckBox hasWarehouseBox = new JCheckBox("Has warehouse");
        JCheckBox hasRoadBox = new JCheckBox("Has road");

        warehouseField.setEnabled(false);
        roadSpinner.setEnabled(false);

        hasWarehouseBox.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                warehouseField.setEnabled(true);
            } else {
                warehouseField.setEnabled(false);
            }
        });

        hasRoadBox.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                roadSpinner.setEnabled(true);
            } else {
                roadSpinner.setEnabled(false);
            }
        });

        deleteCityButton.addActionListener(actionEvent -> {
            int index = cityTable.getSelectedRow();
            int result = JOptionPane.showConfirmDialog(frame, "Do you wish to delete " +
                    cities.get(index).getCityName() + " and all the data associated with it?",
                    "City Deletion", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                //Deletes all the roads from specified city
                deleteAllRoadsFromCity(index);
                //Deletes all the roads incoming to the specified city

                int originId;
                for (int i = roads.size() - 1; i >= 0; --i) {
                    //Finds the roads with Adjacent city Id of the specified city
                    if (roads.get(i).getAdjecencyId() == cities.get(index).getCityId()) {
                        originId = roads.get(i).getOriginCityId();

                        for (City c : cities) {
                            if (c.getCityId() == originId) {
                                c.removeOutgoingRoad(roads.get(i));
                            }
                        }
                        for (Road r : roads) {
                            if( roads.get(i).getOriginCityId() == r.getOriginCityId() &&
                                    roads.get(i).getAdjecencyId() != r.getAdjecencyId()) {
                                r.setPaths(r.getPaths() - 1);
                            }
                        }
                        roads.remove(i);
                    }
                }

                //Deletes the warehouse if one exist in the city
                if (cities.get(index).isHasWarehouse()) {
                    for (Warehouse w : warehouses) {
                        if (w.getCityId() == cities.get(index).getCityId()) {
                            warehouses.remove(w);
                            break;
                        }
                    }
                }
                //Deletes the city itself
                cities.remove(index);

                cityModel.fireTableStructureChanged();
                warehouseModel.fireTableStructureChanged();
                roadModel.fireTableStructureChanged();

                deleteCityButton.setEnabled(false);
                deleteWarehouseButton.setEnabled(false);
                deleteRoadButton.setEnabled(false);

                loadWarehousePanelComboBox();
                loadRoadPanelComboBoxes();
                loadGraphPanelComboBoxes();
            }

        });

        addButton.addActionListener(e -> {
            if ( cityField.getText().equals("") || districtField.getText().equals("") ||
                    (warehouseField.getText().equals("") && hasWarehouseBox.isSelected())) {
                JOptionPane.showMessageDialog(null,"Error:\nOne of the fields is empty",
                        "Bad Input!", JOptionPane.ERROR_MESSAGE);

            } else if (!hasWarehouseBox.isSelected() && !hasRoadBox.isSelected()) {
                cities.add(new City(cityField.getText(), City.getCount() + 1,
                        districtField.getText(), false, false, 0));

                JOptionPane.showMessageDialog(null, "City " + cityField.getText() +
                        " was successfully added!", "City Added", JOptionPane.INFORMATION_MESSAGE);

                cityField.setText("");
                districtField.setText("");

                deleteCityButton.setEnabled(false);

                cityModel.fireTableStructureChanged();

                loadWarehousePanelComboBox();
                loadRoadPanelComboBoxes();
                loadGraphPanelComboBoxes();
            } else if (hasWarehouseBox.isSelected() && !hasRoadBox.isSelected()) {
                cities.add(new City(cityField.getText(), City.getCount() + 1,
                        districtField.getText(), true, false, 0));

                warehouses.add(new Warehouse(Warehouse.getCount() + 1, City.getCount(),
                        warehouseField.getText(), cityField.getText(),
                        false, 0));

                JOptionPane.showMessageDialog(null,
                        "City" + cityField.getText() + " with warehouse " + warehouseField.getText() +
                                " was successfully added!", "City Added", JOptionPane.INFORMATION_MESSAGE);

                cityField.setText("");
                districtField.setText("");
                warehouseField.setText("");
                hasWarehouseBox.setSelected(false);

                deleteCityButton.setEnabled(false);
                deleteWarehouseButton.setEnabled(false);

                cityModel.fireTableStructureChanged();
                warehouseModel.fireTableStructureChanged();

                loadRoadPanelComboBoxes();
                loadGraphPanelComboBoxes();
            } else if (!hasWarehouseBox.isSelected() && hasRoadBox.isSelected()) {
                cities.add(new City(cityField.getText(), City.getCount() + 1,
                        districtField.getText(), false, false, 0));

                for (int i = 0; i < (Integer) roadSpinner.getValue(); ++i) {
                    addRoadToCity(cities.indexOf(cities.get(cities.size() - 1))); //get index of last city
                }

                JOptionPane.showMessageDialog(null, "City " + cityField.getText() +
                                " with " + cities.get(cities.size() - 1).getRoads() + " roads was successfully added!",
                        "City Added", JOptionPane.INFORMATION_MESSAGE);

                cityField.setText("");
                districtField.setText("");
                roadSpinner.setValue(1);
                hasRoadBox.setSelected(false);

                deleteCityButton.setEnabled(false);
                deleteRoadButton.setEnabled(false);

                cityModel.fireTableStructureChanged();
                roadModel.fireTableStructureChanged();

                loadWarehousePanelComboBox();
                loadRoadPanelComboBoxes();
                loadGraphPanelComboBoxes();
            } else if (hasWarehouseBox.isSelected() && hasRoadBox.isSelected()) {
                cities.add(new City(cityField.getText(), City.getCount() + 1,
                        districtField.getText(), true, false, 0));

                warehouses.add(new Warehouse(Warehouse.getCount() + 1, City.getCount(),
                        warehouseField.getText(), cityField.getText(),
                        false, 0));

                for (int i = 0; i < (Integer) roadSpinner.getValue(); ++i) {
                    addRoadToCity(cities.indexOf(cities.get(cities.size() - 1))); //get index of last city
                }

                JOptionPane.showMessageDialog(null, "City " + cityField.getText() +
                                " with " + cities.get(cities.size() - 1).getRoads() + " roads and " +
                                warehouseField.getText() +" warehouse was successfully added!",
                        "City Added", JOptionPane.INFORMATION_MESSAGE);

                cityField.setText("");
                districtField.setText("");
                warehouseField.setText("");
                roadSpinner.setValue(1);
                hasWarehouseBox.setSelected(false);
                hasRoadBox.setSelected(false);

                deleteCityButton.setEnabled(false);
                deleteWarehouseButton.setEnabled(false);
                deleteRoadButton.setEnabled(false);

                cityModel.fireTableStructureChanged();
                warehouseModel.fireTableStructureChanged();
                roadModel.fireTableStructureChanged();

                loadRoadPanelComboBoxes();
                loadGraphPanelComboBoxes();
            }
        });

        hasWarehouseBox.setBackground(new Color(255, 170, 100));
        hasRoadBox.setBackground(new Color(255, 170, 100));

        //Putting components into right side panel
        rightPanel.add(cityLabel);
        rightPanel.add(cityField);

        rightPanel.add(districtLabel);
        rightPanel.add(districtField);

        rightPanel.add(warehouseLabel);
        rightPanel.add(warehouseField);

        rightPanel.add(roadLabel);
        rightPanel.add(roadSpinner);

        //Putting components into left side panel
        leftPanel.add(addButton);
        leftPanel.add(deleteCityButton);

        leftPanel.add(hasWarehouseBox);
        leftPanel.add(filler);
        leftPanel.add(hasRoadBox);

        //Setting up the color and borders for the panels
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPanel.setBackground(new Color(255, 170, 100));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        leftPanel.setBackground(new Color(255, 170, 100));

        mainPanel.add(rightPanel);
        mainPanel.add(leftPanel);
        return mainPanel;
    }

    private JComponent createWarehouseTabPanel() {
        JPanel warehousePanel = new JPanel(new GridLayout(2,1));
        warehousePanel.setPreferredSize(new Dimension(720, 310));

        //Initializing table model and table itself
        warehouseModel = new WarehouseTableModel();
        warehouseTable = new JTable(warehouseModel);

        //Configuring warehouse table
        //warehouseTable.setPreferredScrollableViewportSize(new Dimension(720, 155));
        warehouseTable.setFillsViewportHeight(true);
        warehouseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        warehouseTable.getTableHeader().setReorderingAllowed(false);
        warehouseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);

                int row = warehouseTable.rowAtPoint(mouseEvent.getPoint());

                if (row != -1) {
                    deleteWarehouseButton.setEnabled(true);
                }
            }
        });

        //Putting warehouseTable into a scroll pane
        JScrollPane scrollPane = new JScrollPane(warehouseTable);

        warehousePanel.add(scrollPane);
        warehousePanel.add(createWarehouseManagementPanel());

        return warehousePanel;
    }

    private JPanel createWarehouseManagementPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        JPanel rightPanel = new JPanel(new GridLayout(4, 2, 0, 25));
        JPanel leftPanel = new JPanel(new GridLayout(2, 2, 20, 25));

        JLabel cityLabel = new JLabel("City name: ");
        JLabel warehouseLabel = new JLabel("Warehouse name: ");
        JLabel filler1 = new JLabel("");
        JLabel filler2 = new JLabel("");
        JLabel filler3 = new JLabel("");

        JTextField warehouseField = new JTextField();

        cityComboBoxWarehouse = new JComboBox<>();

        JButton addWarehouseButton = new JButton("Add Warehouse");
        deleteWarehouseButton = new JButton("Delete Warehouse");

        deleteWarehouseButton.setEnabled(false);

        addWarehouseButton.addActionListener(actionEvent -> {
            if (warehouseField.getText().equals("")) {
                JOptionPane.showMessageDialog(frame,"Error:\nWarehouse name cannot be empty", "Bad Input",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                City modifiedCity = (City) cityComboBoxWarehouse.getSelectedItem();

                //if no city found, something went wrong!!
                if (modifiedCity == null) {
                    JOptionPane.showMessageDialog(frame, "Error:\nProblem with data could not get City index",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //adding warehouse and editing city array
                warehouses.add(new Warehouse(Warehouse.getCount() + 1, modifiedCity.getCityId(),
                        warehouseField.getText(), modifiedCity.getCityName(),
                        modifiedCity.isHasRoad(), modifiedCity.getRoads()));
                modifiedCity.setHasWarehouse(true);

                loadWarehousePanelComboBox();

                warehouseField.setText("");

                deleteWarehouseButton.setEnabled(false);
                deleteCityButton.setEnabled(false);
                loadGraphPanelComboBoxes();

                cityModel.fireTableStructureChanged();
                warehouseModel.fireTableStructureChanged();
            }
        });

        deleteWarehouseButton.addActionListener(actionEvent -> {
            Warehouse modifiedWarehouse = warehouses.get(warehouseTable.getSelectedRow());

            int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete " +
                    modifiedWarehouse.getWarehouseName() + " warehouse?", "Warehouse deletion",
                    JOptionPane.OK_CANCEL_OPTION);

            if ( result == JOptionPane.OK_OPTION ) {
                warehouses.remove(modifiedWarehouse);

                for ( City  c : cities ) {
                    if ( modifiedWarehouse.getCityId() == c.getCityId() ) {
                        c.setHasWarehouse(false);
                        break;
                    }
                }

                //updates CityComboBox with new city name values
                loadWarehousePanelComboBox();
                loadGraphPanelComboBoxes();

                warehouseModel.fireTableStructureChanged();
                cityModel.fireTableStructureChanged();

                deleteWarehouseButton.setEnabled(false);
                deleteCityButton.setEnabled(false);
            }
        });

        rightPanel.add(cityLabel);
        rightPanel.add(cityComboBoxWarehouse);

        rightPanel.add(warehouseLabel);
        rightPanel.add(warehouseField);

        rightPanel.add(filler1);

        leftPanel.add(addWarehouseButton);
        leftPanel.add(deleteWarehouseButton);

        leftPanel.add(filler2);
        leftPanel.add(filler3);

        mainPanel.add(rightPanel);
        mainPanel.add(leftPanel);

        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPanel.setBackground(new Color(255, 170, 100));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        leftPanel.setBackground(new Color(255, 170, 100));

        return mainPanel;
    }

    private void loadWarehousePanelComboBox() {
        DefaultComboBoxModel<City> model = new DefaultComboBoxModel<>();
        for (City c : cities) {
            if (!c.isHasWarehouse()) {
                model.addElement(c);
            }
        }
        cityComboBoxWarehouse.setModel(model);
    }

    private JComponent createRoadTabPanel() {
        JPanel roadPanel = new JPanel(new GridLayout(2,1));
        roadPanel.setPreferredSize(new Dimension(720, 310));

        //Initializing table model and table itself
        roadModel = new RoadTableModel();
        roadTable = new JTable(roadModel);

        //Configuring road table
        //roadTable.setPreferredScrollableViewportSize(new Dimension(720, 155));
        roadTable.setFillsViewportHeight(true);
        roadTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roadTable.getTableHeader().setReorderingAllowed(false);
        roadTable.setAutoCreateColumnsFromModel( false );
        roadTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        roadTable.getColumnModel().getColumn(1).setPreferredWidth(40);
        roadTable.getColumnModel().getColumn(2).setPreferredWidth(40);
        roadTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        roadTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        roadTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);

                int row = roadTable.rowAtPoint(mouseEvent.getPoint());
                int column = roadTable.columnAtPoint(mouseEvent.getPoint());

                if (row != -1) {
                    deleteRoadButton.setEnabled(true);
                }

                if (column == 2) {   //Column 2 - 'Paths' column
                    if (mouseEvent.getClickCount() == 2) {
                        int cityIndex = -1;
                        for (City c : cities) {
                            if (roads.get(row).getOriginCityId() == c.getCityId()) {
                                cityIndex = cities.indexOf(c);
                                break;
                            }
                        }
                        if (cityIndex == -1) {
                            JOptionPane.showMessageDialog(null, "Error:\nCould not get cityIndex",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            editNumberOfRoadsPopup(cityIndex);
                        }
                    }
                }
            }
        });

        //Putting roadTable into a scroll pane
        JScrollPane scrollPane = new JScrollPane(roadTable);

        roadPanel.add(scrollPane);
        roadPanel.add(createRoadManagementPanel());

        return roadPanel;
    }

    private JPanel createRoadManagementPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        JPanel rightPanel = new JPanel(new GridLayout(4, 2, 0, 25));
        JPanel leftPanel = new JPanel(new GridLayout(2, 2, 20, 25));

        JLabel originLabel = new JLabel("Origin city name: ");
        JLabel adjacentLabel = new JLabel("Adjacent city name: ");
        JLabel groupLabel = new JLabel("Group name: ");
        JLabel filler1 = new JLabel("");
        JLabel filler2 = new JLabel("");

        originCityComboBox = new JComboBox<>();
        adjacentCityComboBox = new JComboBox<>();
        groupComboBox = new JComboBox<>();

        JButton addRoadButton = new JButton("Add Road");
        deleteRoadButton = new JButton("Delete Road");

        deleteRoadButton.setEnabled(false);

        originCityComboBox.addActionListener(actionEvent ->
                updateAdjCities(cities.indexOf((City) originCityComboBox.getSelectedItem()), adjacentCityComboBox));

        addRoadButton.addActionListener(actionEvent -> {
            if ("".equals(groupComboBox.getSelectedItem())) {
                JOptionPane.showMessageDialog(frame, "Error:\nNeed to specify road group", "Bad Input",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JLabel distanceLabel = new JLabel("Distance KM: ");
                JLabel timeLabel = new JLabel("Time hours: ");
                JLabel fuelLabel = new JLabel("Fuel cost: ");
                JLabel tollLabel = new JLabel("Toll cost: ");
                JLabel driverLabel = new JLabel("Driver cost: ");

                //Initializing JSpinners
                JSpinner distanceSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 5000.00, 0.01));
                JSpinner timeSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 5000.00, 0.01));
                JSpinner fuelSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 5000.00, 0.01));
                JSpinner tollSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 5000.00, 0.01));
                JSpinner driverSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 5000.00, 0.01));

                distanceSpinner.setEditor(new JSpinner.NumberEditor(distanceSpinner, "#.00"));
                timeSpinner.setEditor(new JSpinner.NumberEditor(timeSpinner, "#.00"));
                fuelSpinner.setEditor(new JSpinner.NumberEditor(fuelSpinner, "#.00"));
                tollSpinner.setEditor(new JSpinner.NumberEditor(tollSpinner, "#.00"));
                driverSpinner.setEditor(new JSpinner.NumberEditor(driverSpinner, "#.00"));

                //Formatting spinners so that it would not accept unwanted characters
                JFormattedTextField distanceField = ((JSpinner.NumberEditor) distanceSpinner.getEditor()).getTextField();
                ((NumberFormatter) distanceField.getFormatter()).setAllowsInvalid(false);
                JFormattedTextField timeField = ((JSpinner.NumberEditor) timeSpinner.getEditor()).getTextField();
                ((NumberFormatter) timeField.getFormatter()).setAllowsInvalid(false);
                JFormattedTextField fuelField = ((JSpinner.NumberEditor) fuelSpinner.getEditor()).getTextField();
                ((NumberFormatter) fuelField.getFormatter()).setAllowsInvalid(false);
                JFormattedTextField tollField = ((JSpinner.NumberEditor) fuelSpinner.getEditor()).getTextField();
                ((NumberFormatter) tollField.getFormatter()).setAllowsInvalid(false);
                JFormattedTextField driverField = ((JSpinner.NumberEditor) driverSpinner.getEditor()).getTextField();
                ((NumberFormatter) driverField.getFormatter()).setAllowsInvalid(false);

                JPanel addRoadPanel = new JPanel(new GridLayout(0, 2, 0, 5)); //2 columns and 5px vertical gap

                addRoadPanel.add(distanceLabel);
                addRoadPanel.add(distanceSpinner);

                addRoadPanel.add(timeLabel);
                addRoadPanel.add(timeSpinner);

                addRoadPanel.add(fuelLabel);
                addRoadPanel.add(fuelSpinner);

                addRoadPanel.add(tollLabel);
                addRoadPanel.add(tollSpinner);

                addRoadPanel.add(driverLabel);
                addRoadPanel.add(driverSpinner);

                int result = JOptionPane.showConfirmDialog(frame, addRoadPanel, "Add new road",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if ( (result == JOptionPane.OK_OPTION) && (((Double) distanceSpinner.getValue() == 0) ||
                        ((Double) timeSpinner.getValue() == 0) || ((Double) fuelSpinner.getValue() == 0) ||
                        ((Double) tollSpinner.getValue() == 0) || ((Double) driverSpinner.getValue() == 0))) {
                    JOptionPane.showMessageDialog(frame, "Error:\nOne of inserted values was not defined or equals 0",
                            "Bad Input!", JOptionPane.ERROR_MESSAGE);

                } else if (result == JOptionPane.OK_OPTION) {   //If the input is correct and OK was clicked new road is added
                    City adjacent = (City) adjacentCityComboBox.getSelectedItem();
                    City origin = (City) originCityComboBox.getSelectedItem();

                    if (origin == null || adjacent == null) {
                        JOptionPane.showMessageDialog(frame, "Error:\nProblem with the data, try loading it again",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    //Actually adding new road to the array
                    roads.add(new Road(Road.getCount() + 1, (String) groupComboBox.getSelectedItem(), origin.getCityId(),
                            origin.getCityName(), origin.getRoads(), adjacent.getCityId(), adjacent.getCityName(),
                            (Double) distanceSpinner.getValue(), (Double) timeSpinner.getValue(),
                            (Double) fuelSpinner.getValue(), (Double) tollSpinner.getValue(),
                            (Double) driverSpinner.getValue()));



                    //Increase number of roads in the cities array
                    if(!origin.isHasRoad()) origin.setHasRoad(true);

                    //Adding a new road to the array in the City class
                    origin.addOutgoingRoad(roads.get(roads.size() - 1));

                    //Increase number of roads in warehouse array
                    if (origin.isHasWarehouse()) {
                        for (Warehouse w : warehouses) {
                            if (w.getCityId() == origin.getCityId()) {
                                w.setRoads(w.getRoads() + 1);
                                if (!w.isHasRoads())  w.setHasRoads(true);
                                break;
                            }
                        }
                    }

                    //Increase number of paths in roads array
                    for( Road r : origin.getOutgoingRoads()) {
                        r.setPaths(r.getPaths() + 1);
                    }

                    loadRoadPanelComboBoxes();
                    deleteRoadButton.setEnabled(false);
                    deleteCityButton.setEnabled(false);

                    cityModel.fireTableStructureChanged();
                    roadModel.fireTableStructureChanged();

                    JOptionPane.showMessageDialog(frame, "Road from " + origin.getCityName() +
                            " to " + adjacent.getCityName() + " has been added!", "Road added", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        deleteRoadButton.addActionListener(actionEvent -> {
            Road modifiedRoad = roads.get(roadTable.getSelectedRow());
            int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete road going from " +
                    modifiedRoad.getOriginCityName() + " to " + modifiedRoad.getAdjecencyCityName() + "?",
                    "Road Deletion", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                for (City c : cities) {
                    if (c.getCityId() == modifiedRoad.getOriginCityId()) {
                        deleteRoadFromCity(cities.indexOf(c), modifiedRoad.getAdjecencyCityName());
                        break;
                    }
                }

                loadRoadPanelComboBoxes();

                deleteRoadButton.setEnabled(false);
                deleteCityButton.setEnabled(false);

                cityModel.fireTableStructureChanged();
                roadModel.fireTableStructureChanged();
            }
        });

        groupComboBox.setEditable(true);


        rightPanel.add(originLabel);
        rightPanel.add(originCityComboBox);

        rightPanel.add(adjacentLabel);
        rightPanel.add(adjacentCityComboBox);

        rightPanel.add(groupLabel);
        rightPanel.add(groupComboBox);

        leftPanel.add(addRoadButton);
        leftPanel.add(deleteRoadButton);

        leftPanel.add(filler1);
        leftPanel.add(filler2);


        mainPanel.add(rightPanel);
        mainPanel.add(leftPanel);

        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPanel.setBackground(new Color(255, 170, 100));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        leftPanel.setBackground(new Color(255, 170, 100));

        return mainPanel;
    }

    private void loadRoadPanelComboBoxes() {
        //Setting up origin city combo box with all cities in it
        DefaultComboBoxModel<City> originModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> groupModel = new DefaultComboBoxModel<>();

        for (City c : cities) {
            originModel.addElement(c);
        }

        originCityComboBox.setModel(originModel);

        //Setting up adjacent city combo box with cities who still does not have roads from origin city
        updateAdjCities(cities.indexOf((City) originCityComboBox.getSelectedItem()), adjacentCityComboBox);


        ArrayList<String> groups = new ArrayList<>();
        groupModel.addElement("");
        for (Road r : roads) {
            if (!groups.contains(r.getGroup())) {
                groups.add(r.getGroup());
                groupModel.addElement(r.getGroup());
            }
        }

        groupComboBox.setModel(groupModel);
    }

    private void updateAdjCities(int cityIndex, JComboBox<City> nonAdjacent) {
        //Goes through all the cities checking every single one if they are adjacent or not.
        //If a city is not adjacent, when it appears on JComboBox
        DefaultComboBoxModel<City> model = new DefaultComboBoxModel<>();
        boolean adjFlag = false;
        for (City c : cities) {
            if (!c.equals(cities.get(cityIndex))) {
                for (Road r : cities.get(cityIndex).getOutgoingRoads()) {
                    if (r.getAdjecencyId() == c.getCityId()) {
                        adjFlag = true;
                        break;
                    }
                }
                if (!adjFlag) {
                    model.addElement(c);
                }
            }
            adjFlag = false;
        }
        nonAdjacent.setModel(model);
    }

    private JComponent createGraphPanel() {
        JPanel graphPanel = new JPanel(new GridLayout(2, 1));

        resultArea = new JTextArea();
        //textArea.setColumns(20);
        resultArea.setLineWrap(true);
        //textArea.setRows(5);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);

        JScrollPane jScrollPane1 = new JScrollPane(resultArea);

        graphPanel.add(createGraphManagementPanel());
        graphPanel.add(jScrollPane1);

        return graphPanel;
    }

    private JPanel createGraphManagementPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        JPanel rightPanel = new JPanel(new GridLayout(4, 2, 0, 25));
        JPanel leftPanel = new JPanel(new GridLayout(2, 2, 20, 25));

        JLabel sourceLabel = new JLabel("Origin city name: ");
        JLabel destinationLabel = new JLabel("Destination city name: ");
        JLabel criteriaLabel = new JLabel("Select criteria: ");
        JLabel filler1 = new JLabel("");
        JLabel filler2 = new JLabel("");

        Object [] criteria = {"Distance KM", "Time Hours", "Fuel Cost", "Toll Cost", "Driver Cost"};

        sourceCityComboBox = new JComboBox<>();
        destinationCityComboBox = new JComboBox<>();
        JComboBox<Object> criteriaComboBox = new JComboBox<>(criteria);

        JButton findPathButton = new JButton("Find the best path");

        sourceCityComboBox.addActionListener(actionEvent -> updateDestinationCityComboBox());

        findPathButton.addActionListener(actionEvent -> {
            City source = (City) sourceCityComboBox.getSelectedItem();
            City destination = (City) destinationCityComboBox.getSelectedItem();

            resultArea.setText(new Algorithm(cities, roads)
                    .execute(source, destination, criteriaComboBox.getSelectedIndex()));
        });

        rightPanel.add(sourceLabel);
        rightPanel.add(sourceCityComboBox);

        rightPanel.add(destinationLabel);
        rightPanel.add(destinationCityComboBox);

        rightPanel.add(criteriaLabel);
        rightPanel.add(criteriaComboBox);

        leftPanel.add(findPathButton);
        leftPanel.add(filler1);
        leftPanel.add(filler2);

        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPanel.setBackground(new Color(255, 170, 100));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        leftPanel.setBackground(new Color(255, 170, 100));

        mainPanel.add(rightPanel);
        mainPanel.add(leftPanel);

        return mainPanel;
    }

    private void loadGraphPanelComboBoxes() {
        DefaultComboBoxModel<City> sourceModel = new DefaultComboBoxModel<>();

        for (City c : cities) {
            if (c.isHasWarehouse()) {
                sourceModel.addElement(c);
            }
        }

        sourceCityComboBox.setModel(sourceModel);

        updateDestinationCityComboBox();
    }

    private void updateDestinationCityComboBox() {
        DefaultComboBoxModel<City> destinationModel = new DefaultComboBoxModel<>();

        for (City c : cities) {
            if (!c.equals(sourceCityComboBox.getSelectedItem()) /*&& c.isHasRoad() not sure if needed*/) {
                destinationModel.addElement(c);
            }
        }

        destinationCityComboBox.setModel(destinationModel);
    }

    private void editNumberOfRoadsPopup(int cityIndex) {
        if (cities.get(cityIndex).isHasRoad()) {
            //Creating first dialog window with options to add and delete roads
            Object[] options = {"Add Road", "Delete Road", "Cancel"};
            int n = JOptionPane.showOptionDialog(frame,
                    "Do you wish to change number of roads in " + cities.get(cityIndex).getCityName() +"?",
                    "Edit Roads", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[2]);

            if (n == JOptionPane.YES_OPTION) {
                addRoadToCity(cityIndex);
            } else if (n == JOptionPane.NO_OPTION) {
                Object[] possibilities = new String[cities.get(cityIndex).getOutgoingRoads().size()];

                //Putting all adjacent cities into object array to use it in JOptionPane
                for (int i = 0; i < cities.get(cityIndex).getOutgoingRoads().size(); ++i)
                    possibilities[i] = cities.get(cityIndex).getOutgoingRoads().get(i).getAdjecencyCityName();

                String userInput = (String) JOptionPane.showInputDialog(null,
                        "Choose a city to which you want to delete the road:",
                        "Road Deletion", JOptionPane.WARNING_MESSAGE, null,
                        possibilities, possibilities[0]);
                deleteRoadFromCity(cityIndex, userInput);
            }
        } else {
            addRoadPopup(cityIndex);
        }
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
    Methods to ADD roads managed by user
    --------------------------------------------------------------------------------------------------------------------
     */
    private void addRoadPopup(int cityIndex) {
        Object[] options = {"Add Road", "Cancel"};
        int n = JOptionPane.showOptionDialog(frame,
                "Do you wish to change number of roads in " + cities.get(cityIndex).getCityName() +"?",
                "Edit Roads", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);
        if (n == JOptionPane.YES_OPTION) {
            addRoadToCity(cityIndex);
        }
    }

    private void addRoadToCity(int cityIndex) {
        JLabel originField = new JLabel(cities.get(cityIndex).getCityName());
        JLabel originLabel = new JLabel("Origin city: ");
        JLabel adjacencyLabel = new JLabel("Adjacent city: ");
        JLabel groupLabel = new JLabel("Group: ");
        JLabel distanceLabel = new JLabel("Distance KM: ");
        JLabel timeLabel = new JLabel("Time hours: ");
        JLabel fuelLabel = new JLabel("Fuel cost: ");
        JLabel tollLabel = new JLabel("Toll cost: ");
        JLabel driverLabel = new JLabel("Driver cost: ");

        JComboBox<City> nonAdjacentList = new JComboBox<>();

        //Goes through all the cities checking every single one if they are adjacent or not.
        //If a city is not adjacent, when it appears on JComboBox
        updateAdjCities(cityIndex, nonAdjacentList);

        //Finds groups and puts them into an editable JComboBox
        ArrayList<String> groups = new ArrayList<>();

        JComboBox<String> groupList = new JComboBox<>();

        groupList.addItem("");

        for (Road r : roads) {
            if (!groups.contains(r.getGroup())) {
                groups.add(r.getGroup());
                groupList.addItem(r.getGroup());
            }
        }

        groupList.setEditable(true);

        //Initializing spinners
        JSpinner distanceSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 5000.00, 0.01));
        JSpinner timeSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 5000.00, 0.01));
        JSpinner fuelSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 5000.00, 0.01));
        JSpinner tollSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 5000.00, 0.01));
        JSpinner driverSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 5000.00, 0.01));

        distanceSpinner.setEditor(new JSpinner.NumberEditor(distanceSpinner, "#.00"));
        timeSpinner.setEditor(new JSpinner.NumberEditor(timeSpinner, "#.00"));
        fuelSpinner.setEditor(new JSpinner.NumberEditor(fuelSpinner, "#.00"));
        tollSpinner.setEditor(new JSpinner.NumberEditor(tollSpinner, "#.00"));
        driverSpinner.setEditor(new JSpinner.NumberEditor(driverSpinner, "#.00"));

        //Formatting spinners so that it would not accept unwanted characters
        JFormattedTextField distanceField = ((JSpinner.NumberEditor) distanceSpinner.getEditor()).getTextField();
        ((NumberFormatter) distanceField.getFormatter()).setAllowsInvalid(false);
        JFormattedTextField timeField = ((JSpinner.NumberEditor) timeSpinner.getEditor()).getTextField();
        ((NumberFormatter) timeField.getFormatter()).setAllowsInvalid(false);
        JFormattedTextField fuelField = ((JSpinner.NumberEditor) fuelSpinner.getEditor()).getTextField();
        ((NumberFormatter) fuelField.getFormatter()).setAllowsInvalid(false);
        JFormattedTextField tollField = ((JSpinner.NumberEditor) fuelSpinner.getEditor()).getTextField();
        ((NumberFormatter) tollField.getFormatter()).setAllowsInvalid(false);
        JFormattedTextField driverField = ((JSpinner.NumberEditor) driverSpinner.getEditor()).getTextField();
        ((NumberFormatter) driverField.getFormatter()).setAllowsInvalid(false);

        //Setting up a panel for creating a new road
        JPanel addRoadPanel = new JPanel(new GridLayout(0, 2, 0, 5)); //2 columns and 5px vertical gap
        addRoadPanel.add(originLabel);
        addRoadPanel.add(originField);

        addRoadPanel.add(adjacencyLabel);
        addRoadPanel.add(nonAdjacentList);

        addRoadPanel.add(groupLabel);
        addRoadPanel.add(groupList);

        addRoadPanel.add(distanceLabel);
        addRoadPanel.add(distanceSpinner);

        addRoadPanel.add(timeLabel);
        addRoadPanel.add(timeSpinner);

        addRoadPanel.add(fuelLabel);
        addRoadPanel.add(fuelSpinner);

        addRoadPanel.add(tollLabel);
        addRoadPanel.add(tollSpinner);

        addRoadPanel.add(driverLabel);
        addRoadPanel.add(driverSpinner);

        //End of setting up, newly created panel will be displayed on option pane
        int result = JOptionPane.showConfirmDialog(frame, addRoadPanel, "Add new road",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        //Check for any input mistakes
        if ( (result == JOptionPane.OK_OPTION) && ((groupList.getSelectedItem() == "") || ((Double) distanceSpinner.getValue() == 0) ||
                ((Double) timeSpinner.getValue() == 0) || ((Double) fuelSpinner.getValue() == 0) ||
                ((Double) tollSpinner.getValue() == 0) || ((Double) driverSpinner.getValue() == 0))) {
            JOptionPane.showMessageDialog(frame, "Error:\nOne of inserted values was not defined or equals 0",
                    "Bad Input!", JOptionPane.ERROR_MESSAGE);
        } else if (result == JOptionPane.OK_OPTION) {   //If the input is correct and OK was clicked new road is added
            City adjacent = (City) nonAdjacentList.getSelectedItem();

            if (adjacent == null) {
                JOptionPane.showMessageDialog(frame, "Error:\nSomething wrong with the data. Try restarting the app",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //Actually adding new road to the array
            roads.add(new Road(Road.getCount() + 1, (String) groupList.getSelectedItem(), cities.get(cityIndex).getCityId(),
                    cities.get(cityIndex).getCityName(), cities.get(cityIndex).getRoads(), adjacent.getCityId(),
                    adjacent.getCityName(), (Double) distanceSpinner.getValue(), (Double) timeSpinner.getValue(),
                    (Double) fuelSpinner.getValue(), (Double) tollSpinner.getValue(),
                    (Double) driverSpinner.getValue()));



            //Increase number of roads in the cities array
            if(!cities.get(cityIndex).isHasRoad()) cities.get(cityIndex).setHasRoad(true);

            //Adding a new road to the array in the City class
            cities.get(cityIndex).addOutgoingRoad(roads.get(roads.size() - 1));

            //Increase number of roads in warehouse array
            if (cities.get(cityIndex).isHasWarehouse()) {
                for (Warehouse w : warehouses) {
                    if (w.getCityId() == cities.get(cityIndex).getCityId()) {
                        w.setRoads(w.getRoads() + 1);
                        if (!w.isHasRoads())  w.setHasRoads(true);
                        break;
                    }
                }
            }

            //Increase number of paths in roads array
            for( Road r : cities.get(cityIndex).getOutgoingRoads()) {
                r.setPaths(r.getPaths() + 1);
            }

            deleteCityButton.setEnabled(false);
            deleteRoadButton.setEnabled(false);

            cityModel.fireTableStructureChanged();
            roadModel.fireTableStructureChanged();
        }
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
    End of methods to ADD roads managed by user
    --------------------------------------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------------------------------------
    Methods to DELETE roads managed by user
    --------------------------------------------------------------------------------------------------------------------
     */
    private void deleteRoadFromCity(int cityIndex, String adjCityName) {
        ArrayList<Integer> indices = new ArrayList<>();

        //Goes through the roads belonging to the specified city and saving their indices as they appear in roads array
        for (Road r : cities.get(cityIndex).getOutgoingRoads()) {
            indices.add(roads.indexOf(r));
        }

        if ((adjCityName != null) && (adjCityName.length() > 0)) {

            //Looks for the right index to be deleted in roads array
            for (Integer i : indices) {

                //*This will run only once*
                if (adjCityName.equals(roads.get(i).getAdjecencyCityName())) {
                    //Decreasing number of roads in cities array
                    cities.get(cityIndex).removeOutgoingRoad(roads.get(i));

                    if (cities.get(cityIndex).getRoads() == 0) {
                        cities.get(cityIndex).setHasRoad(false);
                    }

                    //Decreasing number of paths in roads array
                    for (Road r : cities.get(cityIndex).getOutgoingRoads()) {
                        r.setPaths(r.getPaths() - 1);
                    }

                    //Removing selected road from roads array
                    roads.remove(roads.get(i));

                    //Decreasing number of roads in warehouse array
                    if (cities.get(cityIndex).isHasWarehouse()) {
                        for (Warehouse w : warehouses) {
                            if (w.getCityId() == cities.get(cityIndex).getCityId()) {
                                w.setRoads(w.getRoads() - 1);
                                if (w.getRoads() == 0)  w.setHasRoads(false);
                                break;
                            }
                        }
                    }

                    //Updating the tables
                    cityModel.fireTableStructureChanged();
                    warehouseModel.fireTableStructureChanged();
                    roadModel.fireTableStructureChanged();

                    deleteCityButton.setEnabled(false);
                    deleteWarehouseButton.setEnabled(false);
                    deleteRoadButton.setEnabled(false);
                    return;
                }
            }
        }
    }

    private void deleteAllRoadsPopup(int cityIndex) {
        Object[] options = {"Delete Roads", "Cancel"};
        int n = JOptionPane.showOptionDialog(null,
                "Do you wish to delete all roads coming from " + cities.get(cityIndex).getCityName() +"?",
                "Delete Roads", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);
        if (n == JOptionPane.YES_OPTION) {
            deleteAllRoadsFromCity(cityIndex);
        }
    }

    private void deleteAllRoadsFromCity(int cityIndex) {
        //Goes through all roads from specified city and deletes all of them starting with the last one
        for (int i = cities.get(cityIndex).getOutgoingRoads().size() - 1; i >= 0; --i) {
            roads.remove(cities.get(cityIndex).getOutgoingRoads().get(i));

            cities.get(cityIndex).removeOutgoingRoad(cities.get(cityIndex).getOutgoingRoads().get(i));
        }

        //Makes changes to data in city array
        cities.get(cityIndex).setHasRoad(false);

        //Checks and makes changes where needed in warehouse array
        if (cities.get(cityIndex).isHasWarehouse()) {
            for (Warehouse w : warehouses) {
                if (w.getCityId() == cities.get(cityIndex).getCityId()) {
                    w.setRoads(0);
                    w.setHasRoads(false);
                    break;
                }
            }
        }
        //Updating the tables
        cityModel.fireTableStructureChanged();
        warehouseModel.fireTableStructureChanged();
        roadModel.fireTableStructureChanged();

        deleteCityButton.setEnabled(false);
        deleteWarehouseButton.setEnabled(false);
        deleteRoadButton.setEnabled(false);
    }
    /*
    --------------------------------------------------------------------------------------------------------------------
    ****END OF DELETION ALGORITHMS****
    --------------------------------------------------------------------------------------------------------------------
    */
    public static ArrayList<City> getCities() {
        return cities;
    }

    public static ArrayList<Road> getRoads() {
        return roads;
    }

    public static ArrayList<Warehouse> getWarehouses() {
        return warehouses;
    }

    public static WarehouseTableModel getWarehouseModel() {
        return warehouseModel;
    }

    public static RoadTableModel getRoadModel() {
        return roadModel;
    }
}

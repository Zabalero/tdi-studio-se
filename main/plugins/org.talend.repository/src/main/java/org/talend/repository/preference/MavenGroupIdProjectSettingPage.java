// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.core.runtime.maven.MavenConstants;
import org.talend.core.runtime.projectsetting.AbstractProjectSettingPage;
import org.talend.designer.maven.DesignerMavenPlugin;
import org.talend.designer.maven.tools.AggregatorPomsHelper;
import org.talend.designer.maven.utils.PomIdsHelper;
import org.talend.repository.ProjectManager;
import org.talend.repository.i18n.Messages;

/**
 * DOC zwxue class global comment. Detailled comment
 */
public class MavenGroupIdProjectSettingPage extends AbstractProjectSettingPage {

    private Text groupIdText;

    BooleanFieldEditor appendFolderButton;

    private String oldGroupId;

    private boolean oldAppendFolder;

    public MavenGroupIdProjectSettingPage() {
        noDefaultAndApplyButton();
    }

    @Override
    protected String getPreferenceName() {
        return DesignerMavenPlugin.PLUGIN_ID;
    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        parent.setLayout(new GridLayout());

        Composite compsite = new Composite(parent, SWT.NONE);
        compsite.setLayout(new GridLayout(2, false));

        Label label = new Label(compsite, SWT.NONE);
        label.setText(Messages.getString("MavenGroupIdProjectSettingPage.groupIdLabel")); //$NON-NLS-1$
        GridData labelData = new GridData();
        label.setLayoutData(labelData);

        groupIdText = new Text(compsite, SWT.BORDER);
        oldGroupId = getPreferenceStore().getString(MavenConstants.PROJECT_GROUPID);
        groupIdText.setText(oldGroupId);
        GC gc = new GC(groupIdText);
        String defaultGroupId = "org.example." + ProjectManager.getInstance().getCurrentProject().getTechnicalLabel(); //$NON-NLS-1$
        Point labelSize = gc.stringExtent(defaultGroupId);
        gc.dispose();
        int hint = labelSize.x + (ITabbedPropertyConstants.HSPACE * 15);
        GridData textData = new GridData();
        textData.widthHint = hint;
        groupIdText.setLayoutData(textData);
        groupIdText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = groupIdText.getText();
                if (!PomIdsHelper.isValidGroupId(text)) {
                    setErrorMessage(Messages.getString("MavenGroupIdProjectSettingPage.groupIdErrMsg"));
                    setValid(false);
                } else {
                    setErrorMessage(null);
                    setValid(true);
                }

            }
        });

        oldAppendFolder = getPreferenceStore().getBoolean(MavenConstants.APPEND_FOLDER_TO_GROUPID);
        BooleanFieldEditor appendFolderButton = new BooleanFieldEditor(MavenConstants.APPEND_FOLDER_TO_GROUPID,
                Messages.getString("MavenGroupIdProjectSettingPage.appendFolderLabel"), parent); //$NON-NLS-1$
        addField(appendFolderButton);

    }

    @Override
    public boolean performOk() {
        if (groupIdText != null && !groupIdText.isDisposed() && appendFolderButton != null) {
            getPreferenceStore().setValue(MavenConstants.PROJECT_GROUPID, groupIdText.getText());
            if (!oldGroupId.equals(groupIdText.getText()) || oldAppendFolder != appendFolderButton.getBooleanValue()) {
                try {
                    new AggregatorPomsHelper().syncAllPoms();
                } catch (Exception e) {
                    ExceptionHandler.process(e);
                }
            }
        }
        return super.performOk();
    }

}
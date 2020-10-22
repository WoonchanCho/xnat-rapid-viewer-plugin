package org.nrg.xnatx.plugins.rapidViewer.services.impl;

import java.util.List;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatExperimentdataShare;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xft.security.UserI;
import org.nrg.xnatx.plugins.rapidViewer.etc.RapidViewerUserType;
import org.nrg.xnatx.plugins.rapidViewer.utils.PluginCode;
import org.nrg.xnatx.plugins.rapidViewer.utils.PluginException;
import org.nrg.xnatx.plugins.rapidViewer.utils.PluginUtils;
import org.nrg.xnatx.plugins.rapidViewer.utils.Security;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PermissionService {
	public void checkPermission(RapidViewerUserType userType, UserI user, String projectId, String subjectId,
			String experimentId, String experimentLabel) throws PluginException {

		Security.checkProject(user, projectId);
		Security.checkSession(user, experimentId);
		XnatImagesessiondata sessionData = PluginUtils.getImageSessionData(experimentId, user);
		if (!experimentLabel.equals(sessionData.getLabel())) {
			throw new PluginException("Bad Experiment label : " + experimentLabel, PluginCode.HttpUnprocessableEntity);
		}
		if (!subjectId.equals(sessionData.getSubjectId())) {
			throw new PluginException("Bad Subject ID : " + subjectId, PluginCode.HttpUnprocessableEntity);
		}
		Security.checkPermissions(user, sessionData.getXSIType() + "/project", projectId, Security.Read);

		boolean isSessionSharedIntoProject = sessionSharedIntoProject(experimentId, projectId);
		if (!isSessionSharedIntoProject) {
			log.info("Project IDs not equal");
		}
	}

	private boolean sessionSharedIntoProject(String experimentId, String projectId) throws PluginException {
		log.info("OhifViewerApi::sessionSharedIntoProject(" + experimentId + ", " + projectId + ")");
		XnatExperimentdata expData = null;
		XnatImagesessiondata session = null;
		try {
			expData = XnatExperimentdata.getXnatExperimentdatasById(experimentId, null, false);
			session = (XnatImagesessiondata) expData;
		} catch (Exception ex) {
			log.error("Experiment not found: " + experimentId, ex);
			throw new PluginException("Experiment not found: " + experimentId, PluginCode.HttpUnprocessableEntity);
		}

		if (expData.getProject().equals(projectId)) {
			log.info("Experiment " + experimentId + " belongs to project " + projectId);
			return true;
		}

		List<XnatExperimentdataShare> xnatExperimentdataShareList = session.getSharing_share();
		for (XnatExperimentdataShare share : xnatExperimentdataShareList) {
			log.info("Share project ID: " + share.getProject());
			if (share.getProject().equals(projectId)) {
				return true;
			}
		}
		return false;
	}
}

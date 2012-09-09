package org.openmeetings.web.components.admin.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.user.Salutationmanagement;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.persistence.beans.adresses.States;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.lang.FieldLanguage;
import org.openmeetings.app.persistence.beans.user.Salutations;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.web.app.Application;
import org.openmeetings.web.app.WebSession;

public class UserForm extends Form<Users> {

	private final List<Salutations> saluationList = Application.getBean(
			Salutationmanagement.class).getUserSalutations(
			WebSession.getLanguage());
	private final List<FieldLanguage> languageList = Application.getBean(
			FieldLanguageDaoImpl.class).getLanguages();

	private List<Long> getSalutationsIds() {
		ArrayList<Long> saluationIdList = new ArrayList<Long>(
				saluationList.size());
		for (Salutations saluation : saluationList) {
			saluationIdList.add(saluation.getSalutations_id());
		}
		return saluationIdList;
	}

	private String getSaluationLabelById(Long id) {
		for (Salutations saluation : saluationList) {
			if (id.equals(saluation.getSalutations_id())) {
				return saluation.getLabel().getValue();
			}
		}
		throw new RuntimeException("Could not find saluation for id " + id);
	}

	private List<Long> getFieldLanguageIds() {
		ArrayList<Long> languageIdList = new ArrayList<Long>(
				languageList.size());
		for (FieldLanguage language : languageList) {
			languageIdList.add(language.getLanguage_id());
		}
		return languageIdList;
	}

	private String getFieldLanguageLabelById(Long id) {
		for (FieldLanguage language : languageList) {
			if (id.equals(language.getLanguage_id())) {
				return language.getName();
			}
		}
		throw new RuntimeException("Could not find saluation for id " + id);
	}

	public UserForm(String id, Users user) {
		super(id, new CompoundPropertyModel<Users>(user));
		setOutputMarkupId(true);

		add(new TextField<String>("login"));

		// new ChoiceRenderer<Salutations>("label.value", "salutations_id"))

		add(new DropDownChoice<Long>("salutations_id", getSalutationsIds(),
				new IChoiceRenderer<Long>() {
					private static final long serialVersionUID = 1L;

					public Object getDisplayValue(Long id) {
						return getSaluationLabelById(id);
					}

					public String getIdValue(Long id, int index) {
						return "" + id;
					}

				}));

		add(new TextField<String>("firstname"));
		add(new TextField<String>("lastname"));

		add(new DropDownChoice<OmTimeZone>("omTimeZone", Application.getBean(
				OmTimeZoneDaoImpl.class).getOmTimeZones(),
				new ChoiceRenderer<OmTimeZone>("frontEndLabel", "jname")));

		add(new DropDownChoice<Long>("language_id", getFieldLanguageIds(),
				new IChoiceRenderer<Long>() {
					private static final long serialVersionUID = 1L;

					public Object getDisplayValue(Long id) {
						return getFieldLanguageLabelById(id);
					}

					public String getIdValue(Long id, int index) {
						return "" + id;
					}

				}));

		add(new CheckBox("forceTimeZoneCheck"));
		add(new TextField<String>("adresses.email"));
		add(new TextField<String>("adresses.phone"));
		add(new CheckBox("sendSMS"));
		DateTextField age = new DateTextField("age");
		DatePicker datePicker = new DatePicker() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getAdditionalJavaScript() {
				return "${calendar}.cfg.setProperty(\"navigator\",true,false); ${calendar}.render();";
			}
		};
		datePicker.setShowOnFieldClick(true);
		datePicker.setAutoHide(true);
		age.add(datePicker);
		add(age);
		add(new TextField<String>("adresses.street"));
		add(new TextField<String>("adresses.additionalname"));
		add(new TextField<String>("adresses.zip"));
		add(new TextField<String>("adresses.town"));
		add(new DropDownChoice<States>("adresses.states", Application.getBean(
				Statemanagement.class).getStates(), new ChoiceRenderer<States>(
				"name", "state_id")));

		final String field159 = WebSession.getString(159);
		final String field160 = WebSession.getString(160);

		add(new DropDownChoice<Integer>("status", Arrays.asList(0, 1),
				new IChoiceRenderer<Integer>() {

					private static final long serialVersionUID = 1L;

					public Object getDisplayValue(Integer id) {
						if (id.equals(0)) {
							return field159;
						} else if (id.equals(1)) {
							return field160;
						}
						return null;
					}

					public String getIdValue(Integer id, int index) {
						return "" + id;
					}

				}));

		final String field166 = WebSession.getString(166);
		final String field167 = WebSession.getString(167);
		final String field168 = WebSession.getString(168);
		final String field1311 = WebSession.getString(1311);

		add(new DropDownChoice<Long>("level_id", Arrays.asList(1L, 2L, 3L, 4L),
				new IChoiceRenderer<Long>() {

					private static final long serialVersionUID = 1L;

					public Object getDisplayValue(Long id) {
						if (id.equals(1L)) {
							return field166;
						} else if (id.equals(2L)) {
							return field167;
						} else if (id.equals(3L)) {
							return field168;
						} else if (id.equals(4L)) {
							return field1311;
						}
						return null;
					}

					public String getIdValue(Long id, int index) {
						return "" + id;
					}

				}));

		// add a button that can be used to submit the form via ajax
		add(new AjaxButton("ajax-button", this) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so that it is hidden
				// target.add(feedback);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				// target.add(feedback);
			}
		});
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

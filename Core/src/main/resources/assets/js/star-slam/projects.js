var postbox = new ko.subscribable();

ko.extenders.validation = function(target, propertyName) {
  //add some sub-observables to our observable
  target.hasError = ko.observable();
  target.validationMessage = ko.observable();

  //define a function to do validation
  function validate(errors) {
	var result = _.findWhere(errors, {property:propertyName});
	target.hasError(result ? true : false);
	target.validationMessage(result ? result.message : "");
  }

  postbox.subscribe(function(errors) {
		validate(errors);
	}, this, "formErrors")

  //validate whenever the value changes
  target.subscribe(validate);

  //return the original observable
  return target;
};

function Project(data) {
  var self = this;

  self.id = ko.observable(data.id);
  self.name = ko.observable(data.name);
  self.rootPath = ko.observable(data.rootPath);
  self.fileGlob = ko.observable(data.fileGlob);

  self.startScan = function() {
  	$.ajax("/projects/"+self.id()+"/scans", {
  		type: "post"
  		, success: function(result, textStatus, response) {
  			alert('Success');
  		}
  	});
  };
}

function Scan(data) {
	var self = this;
	self.id = ko.observable(data.id);
	self.productionDate = ko.observable(data.productionDate);
	self.fileGlob = ko.observable(data.fileGlob);
	self.status = ko.observable(data.status);
	self.processingTime = ko.observable(data.processingTime);
	self.rootPath = ko.observable(data.rootPath);
	self.completionTime = ko.observable(data.completionTime);
	self.initiatedTime = ko.observable(data.initatedTime);
}

function NewProjectViewModel() {
  var self = this;

  self.name = ko.observable().extend({validation: "name"});
  self.rootPath = ko.observable().extend({validation: "rootPath"});
  self.fileGlob = ko.observable().extend({validation: "fileGlob"});

  self.create = function() {
	var projectObj = { name:self.name(), rootPath:self.rootPath(), fileGlob:self.fileGlob()};
	$.ajax("/projects", {
	  data:JSON.stringify(projectObj)
	  , dataType: 'json'
	  , type: "post"
	  , contentType:"application/json; charset=utf-8"
	  , success: function(result, textStatus, response) {
		$.getJSON(response.getResponseHeader("Location"), function(project) {
			postbox.notifySubscribers(new Project(project), "projectCreated");
			$('#new-project-modal').foundation('reveal','close');
		});
		$('#projectForm').get(0).reset();
	  }, error: function(response) {
		var data = JSON.parse(response.responseText);
		postbox.notifySubscribers(data.errors, "formErrors")
	  }
	});
  };
}

function ProjectListViewModel() {
  //Data
  var self = this;
  self.projects = ko.observableArray([]);

  //Load Projects
  $.getJSON("/projects", function(allData) {
	var mappedProjects = $.map(allData, function(item) { return new Project(item) });
	self.projects(mappedProjects)
  })

  postbox.subscribe(function(newValue) {
	self.projects.push(newValue);
  }, self, "projectCreated");
}

function ProjectDetailViewModel() {
	//Data
	var self = this;
	self.scans = ko.observableArray([]);

	//Load Scans
	$.getJSON("/projects")
}

function FormError(id, message) {
	$('#'+id).addClass('error');
}

Sammy(function() {
	this.get('#:folder', function() {
		self.chosenFolderId(this.params.folder);
		self.chosenMailData(null);
		$.get("/mail", { folder: this.params.folder }, self.chosenFolderData);
	});

	this.get('#:folder/:mailId', function() {
		self.chosenFolderId(this.params.folder);
		self.chosenFolderData(null);
		$.get("/mail", { mailId: this.params.mailId }, self.chosenMailData);
	});

	this.get('', function() { this.app.runRoute('get', '#Inbox') });
}).run();

ko.applyBindings(new ProjectListViewModel(), $('#projectList')[0]);
ko.applyBindings(new NewProjectViewModel(), $('#projectForm')[0]);
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

  self.goToDetails = function(project) {
  	location.hash = project.id();
  }
}

function Scan(data) {
	var self = this;
	self.id = ko.observable(data.id);
	self.productionDate = ko.observable(data.productionDate);
	self.fileGlob = ko.observable(data.fileGlob);
	self.status = ko.observable(data.status);
	self.processingTime = ko.observable(data.processingTime);
	self.rootPath = ko.observable(data.rootPath);
	self.completionTime = ko.observable(moment(data.completionTime));
	self.initiatedTime = ko.observable(moment(data.initiatedTime));

	self.processingTimeSeconds = ko.computed(function() {
		return self.processingTime() / 1000;
	})
}

function View(templateName, data) {
	var self = this;
	self.data = data;
	self.templateName = templateName;
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

function ProjectDetailViewModel(project) {
	//Data
	var self = this;
	self.project = project;
	self.scans = ko.observableArray([]);

	//Load Scans
	$.getJSON("/projects/"+project.id()+"/scans", function(scanData) {
		var mappedScans = $.map(scanData, function(item) { return new Scan(item) });
		self.scans(mappedScans);
	})
}

function FormError(id, message) {
	$('#'+id).addClass('error');
}

function ContentViewModel() {
	var self = this;
	self.projectListView = new View("projectListTemplate", new ProjectListViewModel());
	self.selectedView = ko.observable();

	Sammy(function() {
    	this.get('#:project', function() {
    		$.get("/projects/"+this.params.project, {}, function(data, textStatus, xhr) {
				self.selectedView(new View("projectDetailsTemplate", new ProjectDetailViewModel(new Project(data))));
			});
    	});

    	this.get('', function() {
    		self.selectedView(self.projectListView);
    	});
    }).run();
};

ko.applyBindings(new ContentViewModel(), $('#content')[0])
ko.applyBindings(new NewProjectViewModel(), $('#projectForm')[0]);
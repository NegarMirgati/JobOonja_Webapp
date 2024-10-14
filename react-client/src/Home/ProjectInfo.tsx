import React, { Component, Props } from "react";
import ProjectTime from "./ProjectTime";
import Blur from "react-css-blur";
import axios, * as others from 'axios';
import { toast } from "react-toastify";

export default class ProjectInfo extends Component<any, State> {
  constructor(props: any) {
    super(props);
    this.state = {
      projectId: "",
      title: "",
      description: "",
      imageURL: "",
      skills: [],
      budget: 0,
      deadline: 0,
      linkToProPage: ""
    };
  }

  componentDidMount() {
    // console.log("this.props.skills in did mount");
    // console.log(this.props.skills);
    //var key = Object.keys(this.props)[0];
    this.setState({ projectId: this.props.id });
    this.setState({ title: this.props.title });
    this.setState({ description: this.props.description });
    this.setState({ imageURL: this.props.imageURL });
    this.setState({ skills: this.props.skills });
    this.setState({ budget: this.props.budget });
    this.setState({ deadline: this.props.deadline });
    var link = "/project?id=" + this.props.id;
    this.setState({ linkToProPage: link });
    // console.log(this.state);
  }

  createSkills = (): any => {
    var keys: string[] = [];
    // console.log("this.props.skills in create skills");
    // console.log(this.props.skills);
    // console.log("this.props");
    // console.log(this.props);
    for (var i = 0; i < this.props.skills.length; i++) {
      Object.keys(this.props.skills[i]).map(key => {
        const value = this.props.skills[i][key];
        var test = key.concat(", ", value);
        keys.push(test);
      });
    }
    var skillsJSX: JSX.Element[] = [];
    var num = keys.length;
    for (var i = 0; i < keys.length; i++) {
      var key = keys[i];
      skillsJSX.push(
        <button id="btn" type="button" className="btn-xs button-style">
          {key.split(",")[0]}
        </button>
      );
    }
    return skillsJSX;
  };

  handleProjectClick = () => {
    const config = {
      headers: { Authorization: "Bearer " + localStorage.getItem("jwt") }
    };

    const link = `http://localhost:8080/jobKhar_war/project?id=${this.props.projectId}`;
    axios
      .get(link, config)
      .then((response: any) => {
        window.location.href = "/project?id=" + this.props.projectId; // Navigate to the project page
        console.log(this.state.linkToProPage);
      })
      .catch(error => {
        if (error.status === 403) {
          toast.error("شما اجازه دسترسی به این پروژه را ندارید!!!");
        } else if (error.status === 401) {
          toast.error("لطفاً وارد حساب خود شوید.");
        }
        else if (error.status === 404) {
          toast.error("پروژه مورد نظر یافت نشد");
        }
        else {
          toast.error("خطای غیر منتظره ای رخ داده است.");
        }
      });
  };


  render() {
    return (
      <div className="row">
        <Blur radius={this.props.deadline < Date.now() ? "0.8px" : "0"}>
          <div className="project-box">
            <a id="project" onClick={this.handleProjectClick}>
              <div className="col">
                <img
                  className="img-rounded project-image img-rounded img-responsive"
                  src={this.props.imageURL}
                  alt=""
                />
              </div>
              <div className="col">
                <p className="project-time">
                  {" "}
                  {this.props.deadline > Date.now() ? (
                    <button
                      id="btnT"
                      type="button"
                      className="btn-xs button-style"
                    >
                      <ProjectTime {...this.props} />
                    </button>
                  ) : (
                    <button
                      id="btnT"
                      type="button"
                      className="btn-xs button-style-time-out"
                    >
                      <ProjectTime {...this.props} />
                    </button>
                  )}
                </p>
                <p className="project-title"> {this.props.title}</p>

                <p className="project-description ">
                  {" "}
                  {this.props.description}
                </p>
                <p className="project-budget ">
                  بودجه: {this.props.budget} تومان{" "}
                </p>
                <p className="project-skills ">
                  مهارت ها: {this.createSkills()}
                </p>
              </div>
            </a>
          </div>
        </Blur>
      </div>
    );
  }
}

interface State {
  projectId: any;
  title: "";
  description: "";
  imageURL: "";
  skills: any[];
  budget: number;
  deadline: number;
  linkToProPage: any;
}

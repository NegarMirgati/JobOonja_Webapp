import React, { Component } from "react";
import { RouteComponentProps, withRouter } from "react-router";
import queryString from "query-string";
import Footer from "../Common/Footer";
import Header from "../Common/Header";
import { toast } from "react-toastify";
import "bootstrap/dist/css/bootstrap.min.css";
import "../Styles/style.css";
import ProjectCommon from "./ProjectCommon";
import ProjectWindow from "./ProjectWindow";
import axios, * as others from 'axios';

class Project extends Component<RouteComponentProps<any>, State> {
  constructor(props: any) {
    super(props);
    this.state = {
      isProjectAvailable: false,
      alreadyBade: "",
      projectId: "",
      title: "",
      description: "",
      imageURL: "",
      skills: [],
      budget: 0,
      deadline: 0,
      hasBade: ""
    };
  }

  componentDidMount() {
    const parsed = queryString.parse(this.props.location.search);
    const link = `http://localhost:8080/jobKhar_war/project?id=${parsed.id}`;

    const config = {
      headers: { Authorization: "Bearer " + localStorage.getItem("jwt") }
    };

    axios
      .get(link, config)
      .then((response: any) => {
        console.log("Success fetching project data");

        const obj: any = response.data; // Ensure response.data is correct here
        this.setState({
          projectId: parsed.id as string, // Cast if needed
          title: obj.title,
          description: obj.description,
          imageURL: obj.imageURL,
          skills: obj.skills,
          budget: obj.budget,
          deadline: obj.deadline,
          hasBade: obj.hasBade,
          isProjectAvailable: true,
        });
      })
      .catch((error: any) => {
        const status = error.response.status;

        if (status === 404 || status === 403) {
          toast.error("شما اجازه دسترسی به این پروژه را ندارید.");
        } else if (status === 401) {
          toast.error("لطفاً وارد حساب خود شوید.");
        }

        console.log("Error status:", status);
        console.log("پروژه مورد نظر یافت نشد");
      });
  }

  render() {
    console.log("Rendering project");
    console.log(this.state.skills);
    return (
      <div className="page-container">
        <Header />
        <div id="content-wrap">
          <ProjectCommon />
          {this.state.isProjectAvailable && <ProjectWindow {...this.state} />}
        </div>
        <Footer />
      </div>
    );
  }
}

export default withRouter(Project);

interface State {
  isProjectAvailable: boolean;
  alreadyBade: string;
  projectId: string;
  title: string;
  description: string;
  imageURL: string;
  skills: any[];
  budget: number;
  deadline: number;
  hasBade: any;
}

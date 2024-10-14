import React, { Component, ReactNode } from "react";
import { toast } from "react-toastify";
import UserCommon from "./UserCommon";
import Footer from "../Common/Footer";
import Header from "../Common/Header";
import SkillsLogged from "./SkillsLogged";
import Loader from "../Common/Loader";
import "bootstrap/dist/css/bootstrap.min.css";
import "../Styles/style.css";
import "../Styles/userLoggedStyle.css";
import axios, * as others from 'axios';

export default class UserComponentLogged extends Component<any, State> {
  constructor(props: any) {
    super(props);
    this.state = {
      id: "",
      name: "",
      lastname: "",
      job: "",
      bio: "",
      proLink: "",
      skills: [],
      possibleSkills: [],
      loading: true
    };
  }

  componentDidMount() {
    var config = {
      headers: { Authorization: "bearer " + localStorage.getItem("jwt") }
    };
    var linktmp = "http://localhost:8080/jobKhar_war/user?id=";
    if (this.props.userId) {
      var link = linktmp.concat(this.props.userId);
      axios
        .get(link, config, null)
        .then((response: any) => {
          let obj: any = JSON.parse(JSON.stringify(response.data));
          this.setState({ name: obj["name"] });
          this.setState({ lastname: obj["lastname"] });
          this.setState({ id: obj["id"] });
          this.setState({ job: obj["jobTitle"] });
          this.setState({ bio: obj["bio"] });
          this.setState({ proLink: obj["proLink"] });
          this.setState({ skills: obj["skills"] });
          this.setState({ possibleSkills: obj["possibleSkills"] });
          this.setState({ loading: false });
        })
        .catch(function(error: any) {
          toast.error("خطا در برقراری ارتباط با سرور");
        });
    }
  }

  getMainElements() {
    if (this.state.loading) {
      return <Loader />;
    } else {
      return (
        <div id="fill-view-point">
          <Header />
          <UserCommon {...this.state} />
          <SkillsLogged {...this.state} />
          <Footer />
        </div>
      );
    }
  }

  render() {
    document.body.classList.add("htmlBodyStyle");
    return <div>{this.getMainElements()}</div>;
  }
}

interface State {
  id: "";
  name: "";
  lastname: "";
  job: "";
  bio: "";
  proLink: "";
  skills: any[];
  possibleSkills: any[];
  loading;
}

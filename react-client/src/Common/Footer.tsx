import React, {Component} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../Styles/style.css'
class Footer extends Component{
    render(){
        return(
            <div className = "row">
                <div className = "footer">
                    <div className = "container">
                        <div className = "col-sm-12">
                            <p> تمامی حقوق این سایت متعلق به جاب اونجا می باشد © </p>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}
export default Footer;
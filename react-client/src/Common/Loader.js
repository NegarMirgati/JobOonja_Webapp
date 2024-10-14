import React from 'react';
import { css } from '@emotion/react';
// First way to import
import {RingLoader} from 'react-spinners';
// Another way to import
 
const override = css`
    display: block;
    border-color: red;
    margin-top : 20%;
    margin-left : 45%;
    margin-bottom : 20%;
    position : fixed;
`;
const style = { position: "fixed", top: "50%", left: "50%", transform: "translate(-50%, -50%)" };
 
export default class Loader extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true
    }
  }
  render() {
    return (
      <div className= "container-fluid main">
      <div className='sweet-loading' style={style}>
        <RingLoader
          css={override}
          sizeUnit={"px"}
          size={150}
          color={'#1184b2'}
          loading={this.state.loading}
        />
      </div> 
    </div>
    )
  }
}
import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import ViewPersonalInfo from '../../../Modals/MyPosts/View/ViewPersonalInfo';
import requestToPath from '../../../Service/fetchService';
import { useUser } from '../../../UserProvider/UserProvider';
import PersonalInfo from './PersonalInfo';

import '../userProfile.css';

const ViewAboutMain = () => {
    const user = useUser();
    const {visitUserId} = useParams();

    const[personalInfo, setPersonalInfo] = useState({});
    const[show, setShow] = useState(false);

    function handleClose(){setShow(false);}
    function handleShow(){setShow(true);}

    useEffect(() => {
        requestToPath(`/api/UsersInfo/user/${visitUserId}`, "GET", user.jwt)
        .then((userResponse) => {
            let userData = userResponse;
            if(userData.myBio === null) userData.myBio = "";
            if(userData.email === null) userData.email = "";
            if(userData.phone === null) userData.phone = "";
            setPersonalInfo(userData);
        })
        .catch((error) => {console.log(error);})
    }, [visitUserId])

    function displayUserInfo(){
        if(personalInfo){
            return <div key={personalInfo.id}><PersonalInfo personalInfo={personalInfo} /></div>
        }
        else{
            return <div>Create an empty component for personal info which has no info</div>
        }
    }

    return (
        <div className='custom-profileWrapper custom-aboutGridEach mt-2 mb-2' >
            <div className='d-flex justify-content-between mb-1'  >
            <h3 className=' mb-3 '>About</h3>
            <Button size='sm' onClick={() => {handleShow()}}>More</Button>
            </div>
            {/*  displays user info */}
            {displayUserInfo()}
            <ViewPersonalInfo show={show} emitHandleClose={handleClose} personalInfo={personalInfo} />
        </div>
    );
};

export default ViewAboutMain;
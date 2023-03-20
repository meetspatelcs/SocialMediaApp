import React, { useEffect, useState } from 'react';
import { Card } from 'react-bootstrap';
import { useUser } from '../../UserProvider/UserProvider';

import "./myPosts.css";

const ProfileAllImg = (props) => {
    const user = useUser();
    const {currImg} = props;
    
    const [myurl, setMyUrl] = useState();

    useEffect(() => {
        fetch(`/api/posts/${currImg.id}/postImages`, {headers: {Authorization: `Bearer ${user.jwt}`}, method: "GET"})
            .then((dataBuffer) => {
                return dataBuffer.blob();
            })
            .then((data) => { 
                setMyUrl(URL.createObjectURL(data));
                return data;
            })
            .catch((error) => {console.log(error);})
    }, [])

    function displayImg(){
        return <Card.Img className='' src={myurl}  />;
    }

    return (
        <Card className='d-flex align-items-center' style={{border: "none"}}>
            <Card.Body>
                {displayImg()}
            </Card.Body>
        </Card>
    );
};

export default ProfileAllImg;
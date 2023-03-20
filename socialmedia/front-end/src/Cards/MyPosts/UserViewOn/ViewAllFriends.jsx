import React, { useEffect } from 'react';
import { Button, Card } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useUser } from '../../../UserProvider/UserProvider';

const ViewAllFriends = (props) => {
    const user = useUser();

    const {userId, visitUserId} = useParams();
    const {currUser} = props;
    const navigate = useNavigate();
    

    const firstname = currUser.firstname;
    const lastname = currUser.lastname;

    function navigateToUser(){
        // populate relationship between all logged user and user list, pass relationship with navigate
        // if realationship is coming from props, just pass the relationship, no need to fetch from backend

        navigate(`/users/${userId}/user/${currUser.id}/profile`);
    }

    function displayViewBtn(){
        return <div className='text-center'><Button className='w-100' onClick={() => {navigateToUser()}}>View</Button></div>
    }

    function handleButton(){
        if(userId != currUser.id){
            return displayViewBtn();
        }

        return <div className='text-center'><Button className='w-100' disabled={true} style={{backgroundColor:"white", border:"none"}}>View</Button></div>
    }

    return (
        <Card>
            <Card.Body>
                <Card.Title>{firstname} {lastname}</Card.Title>
                    {handleButton()}
            </Card.Body>
        </Card>
    );
};

export default ViewAllFriends;
import React from 'react';
import { Button, Card } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import requestToPath from '../../Service/fetchService';
import { useUser } from '../../UserProvider/UserProvider';

const FriendList = (props) => {
    const user = useUser();
    const {userId, visitUserId} = useParams();
    const visitUser = null;
    
    const {friendId, firstname, lastname, myFriends, compStatus} = props;
    const navigate = useNavigate();

    function validateFriend(){
       return myFriends.some(eachFriend => eachFriend.id == friendId);
    }

    function addRequest(){
        requestToPath(`/api/friends/add/${friendId}`, "POST", user.jwt)
            .then((addResponse) => { setTimeout(() => { validateFriend(); }, 3000) })
            .catch((error) => {console.log(error);})
    }

    function handleViewFriend(){
        navigate(`/users/${userId}/user/${friendId}/profile`, {state:{compStatus}})      
    }

    function addBtn(){
        return <div className='text-center'>
             <Button style={{width: "75px"}} size="sm" className='mt-1' onClick={() => {addRequest();}}>Add</Button>
        </div>
    }

    function friendsBtn(){
        return <div className='text-center'>
                    <Button style={{width: "75px", backgroundColor:"white", border:"none"}} 
                    disabled={true} size="sm" className='mt-1' variant="secondary">Friends</Button>
                </div>
    }

    function viewBtn(){
        return <div className='text-center'>
            <Button style={{width: "75px"}} size="sm" className='mt-1' onClick={() => {handleViewFriend()}}>View</Button>
        </div>
    }

    function handleButton(){
        if(!visitUserId){ return <div>{friendsBtn()}{viewBtn()}</div> }
        if(friendId != userId){
            if(validateFriend() == false){
                return <div>{addBtn()}{viewBtn()}</div>
            }
            return <div>{friendsBtn()}{viewBtn()}</div>;
        }
        return <div>{friendsBtn()}{friendsBtn()}</div>
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

export default FriendList;
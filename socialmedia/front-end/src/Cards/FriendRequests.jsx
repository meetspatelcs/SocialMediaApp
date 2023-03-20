import React, { useEffect, useRef, useState } from 'react';
import { Button, Card } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';

const FriendRequests = (props) => {
    const user = useUser();
    const {userId} = useParams();
    const navigate = useNavigate();

    const { friendId, firstname, lastname, compStatus, selectedFriendId, emitCurrReq, emitTest } = props;
    const friendNullState = {id: null, requestedUser: null, status: null, user: null};
    
    const[friend, setFriend] = useState(friendNullState);
    const[friendStatuses, setFriendStatuses] = useState([]);
    const prevFriend = useRef(friend);
    
    function cancelRequest(){
        requestToPath(`/api/friends/${friendId}`, "DELETE", user.jwt)
            .then((myResponse) => {})
            .catch((error) => {window.location.reload();})
    }    

    function removeRequest(){
        requestToPath(`/api/friends/${selectedFriendId}/unfriend`, "DELETE", user.jwt)
            .then((removeResponse) => {})
            .catch((error) => {console.log(error);})
    }

    function handleViewFriend(){
        navigate(`/users/${userId}/user/${selectedFriendId}/profile`, {state:{compStatus}});
    }

    useEffect(() => {
        if(prevFriend.current.status !== friend.status){ approveRequest(friend.id); }
        prevFriend.current = friend;
    }, [friend])

    function handleAddFriend(){
        if(selectedFriendId !== null && selectedFriendId !== undefined){
            requestToPath(`/api/friends/add/${selectedFriendId}`, "POST", user.jwt)
            .then((myResponse) => { })
            .catch((error) => {console.log(error);})
        }
    }

    function handleApproveFriend(){
        requestToPath(`/api/friends/${friendId}`, "GET", user.jwt)
            .then((friendResponse) => {
                let friendData = friendResponse.friend;
                setFriend(friendData);
                setFriendStatuses(friendResponse.statusEnums);
            })
            .catch((error) => {console.log(error);})
    }

    function approveRequest(friendId){
        if(friend.status !== friendStatuses[1].stauts)
            friend.status = friendStatuses[1].status;

        requestToPath(`/api/friends/${friendId}`, "PUT", user.jwt, friend)
            .then((friendData) => { emitTest(friendData); })
            .catch((error) => {console.log(error);})
    }

    function addBtn(){ return <Button className='w-100 mt-1' onClick={() => {handleAddFriend(); emitCurrReq();}}>Add</Button> }

    function approveBtn(){ return <Button className='w-100 mt-1' onClick={() => {handleApproveFriend();}}>Approve</Button> }

    function cancelBtn(){ return <Button className='w-100 mt-1' variant='danger' onClick={() => {cancelRequest();}}>Cancel</Button> }

    function pendingBtn(){ return <Button className='w-100 mt-1' disabled={true} variant='secondary'>Pending</Button> }

    function removeBtn(){ return <Button className='w-100 mt-1' variant='danger' onClick={() => {removeRequest();}}>Remove</Button> }

    function viewBtn(){ return <Button className='w-100 mt-1' onClick={() => {handleViewFriend()}}>View</Button> }

    function handleBtn(){
        if(compStatus == 'Pending'){ return <div>{pendingBtn()}{cancelBtn()}</div> }
        if(compStatus == 'Friends'){ return <div>{removeBtn()}{viewBtn()}</div> }
        if(compStatus == 'Received'){ return <div>{approveBtn()}{cancelBtn()}</div> }
        if(compStatus == '' || compStatus == null){ return <div>{addBtn()}{viewBtn()}</div> }
    }

    return (
        <div>
            <Card className='d-flex flex-column'>
                <Card.Body>
                    <Card.Title>{firstname} {lastname}</Card.Title>
                    {handleBtn()}
                </Card.Body>
            </Card>
        </div>
    );
};

export default FriendRequests;
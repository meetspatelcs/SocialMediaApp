import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import requestToPath from '../Service/fetchService';
import { useUser } from '../UserProvider/UserProvider';

const RequestsHandler = (props) => {
    const user = useUser();
    const {userId, visitUserId, friendStatus} = props;

    const [isLoggedUser, setIsLoggedUser] = useState(true);
    const [friendEntity, setFriendEntity] = useState(null);
    const [enumStatus, setEnumStatus] = useState([]);
    const [newStatus, setNewStatus] = useState('');
    
    function validateFriend(){
        requestToPath(`/api/friends/user/${visitUserId}/validateFriend`, "GET", user.jwt)
            .then((friendResponse) => {
                setNewStatus(friendResponse.status);
                if(userId != friendResponse.user.id){setIsLoggedUser(false);}
                if(friendEntity == null){setFriendEntity(friendResponse);}
            })
            .catch((error) => { console.log(error); })
    }

    function handleAdd(){
        if(friendEntity == null){
            requestToPath(`/api/friends/add/${visitUserId}`, "POST", user.jwt)
                .then((addResponse) => { setNewStatus('Pending'); })
                .catch((error) => {console.log(error);})
        }
    }

    function handleCurrStatus(){
        if(friendEntity.status !== enumStatus[1].status){
            friendEntity.status = enumStatus[1].status;
        }
    }

    function handleApprove(){
        if(friendEntity != null){
            handleCurrStatus();
            requestToPath(`/api/friends/${friendEntity.id}`, "PUT", user.jwt, friendEntity)
                .then((approveResponse) => { setNewStatus('Friends'); })
                .catch((error) => {console.log(error);})
        }
    }

    function handleEnum(){
        if(friendEntity != null){
            requestToPath(`/api/friends/${friendEntity.id}`, "GET", user.jwt)
                .then((enumResponse) => { setEnumStatus(enumResponse.statusEnums); })
                .catch((error) => {console.log(error);})
        }
    }

    function handleCancel(){
        if(friendEntity != null){
            requestToPath(`/api/friends/${friendEntity.id}`, "DELETE", user.jwt)
                .then((cancelResponse) => { setNewStatus(''); })
                .catch((error) => { setNewStatus(''); })
        }
    }

    function addBtn(){ return <Button onClick={() => {handleAdd()}}>Add</Button> }
    
    function approveBtn(){ return <Button onClick={() => {handleEnum()}}>Approve</Button> }
   
    function cancelBtn(){ return <Button variant='danger' onClick={() => {handleCancel()}}>Cancel</Button> }
   
    function removeBtn(){ return <Button variant='danger' onClick={() => {handleCancel()}}>Remove</Button> }

    function handleRequestBtn(){    
        if(friendStatus == 'Friends' || newStatus == 'Friends'){ return <div className='viewMP-reqBtn'>{removeBtn()}</div> }    
        if(friendStatus == 'Pending' || newStatus == 'Pending'){
            
            validateFriend();
            if(isLoggedUser){return <div className='viewMP-reqBtn'>{cancelBtn()}</div>}
            return <div className='d-flex justify-content-between viewMP-reqBtn'>{approveBtn()} {cancelBtn()}</div>
        }
        return <div className='viewMP-reqBtn'>{addBtn()}</div>
    }

    useEffect(() => {
      if(friendStatus == ''){
        validateFriend();
      }
    }, [newStatus])

    useEffect(() => {
        if(enumStatus != ''){
            handleApprove();
        }
    }, [enumStatus])

    return (
        <div>
            {handleRequestBtn()}        
        </div>
    );
};

export default RequestsHandler;